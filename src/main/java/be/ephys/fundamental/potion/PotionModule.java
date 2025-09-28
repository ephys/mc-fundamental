package be.ephys.fundamental.potion;

import be.ephys.cookiecore.config.Config;
import be.ephys.fundamental.Mod;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(
  modid = be.ephys.fundamental.Mod.MODID,
  bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD
)
public class PotionModule {
  @Config(name = "enable_cleanse_potion", description = "Adds a 'cleanse' potion that cures all negative effects (requires restart).")
  @Config.BooleanDefault(true)
  public static ForgeConfigSpec.BooleanValue enableCleansePotion;

  @Config(name = "potion_stack_size", description = "Configures the stack size of regular potions (requires restart).")
  @Config.IntDefault(value = 1, min = 0, max = 64)
  public static ForgeConfigSpec.IntValue potionStackSize;

  @Config(name = "persistent_potions", description = "Potions that persist after death. This list needs to contain the registry names of the potions, e.g. 'minecraft:swiftness', 'minecraft:long_swiftness', etc... (requires restart)")
  @Config.StringListDefault({})
  public static ForgeConfigSpec.ConfigValue<List<String>> persistentPotionList;

  public static final String TAG_EFFECTS_ON_DEATH = "fundamental:effects_on_death";

  public static final RegistryObject<MobEffect> CURE_EFFECT = Mod.MOB_EFFECTS.register("cure", () -> {
    return new CurativeEffect(MobEffectCategory.BENEFICIAL, 0x98D982);
  });

  public static final RegistryObject<Potion> CURE_POTION = Mod.POTIONS.register("cure",
    () -> new Potion(new MobEffectInstance(CURE_EFFECT.get())));

  @SubscribeEvent
  public static void onModSetup(FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(PotionModule::onPlayerDeath);
    MinecraftForge.EVENT_BUS.addListener(PotionModule::onPlayerRespawn);
    MinecraftForge.EVENT_BUS.addListener(PotionModule::onItemTooltip);

    registerPotionRecipe();
    registerPersistentPotions();

    Items.POTION.maxStackSize = potionStackSize.get();
  }

  private static void registerPersistentPotions() {
    List<String> persistentPotions = persistentPotionList.get();
    if (persistentPotions.isEmpty()) {
      return;
    }

    for (Map.Entry<ResourceKey<Potion>, Potion> potionEntry : ForgeRegistries.POTIONS.getEntries()) {
      if (!persistentPotions.contains(potionEntry.getKey().location().toString())) {
        continue;
      }

      for (MobEffectInstance effect : potionEntry.getValue().getEffects()) {
        ((IPersistentEffect) effect).mc_fundamental$setPersistsAfterDeath(true);
      }
    }
  }

  private static void registerPotionRecipe() {
    if (!enableCleansePotion.get()) {
      return;
    }

    ItemStack awkwardPotion = Items.POTION.getDefaultInstance();
    PotionUtils.setPotion(awkwardPotion, Potions.AWKWARD);

    ItemStack curePotion = Items.POTION.getDefaultInstance();
    PotionUtils.setPotion(curePotion, PotionModule.CURE_POTION.get());

    BrewingRecipeRegistry.addRecipe(Ingredient.of(awkwardPotion), Ingredient.of(Items.MILK_BUCKET), curePotion);
  }

  public static void onItemTooltip(ItemTooltipEvent event) {
    if (!event.getItemStack().is(Items.POTION)) {
      return;
    }

    Collection<MobEffectInstance> effects = PotionUtils.getMobEffects(event.getItemStack());

    boolean hasPersistentEffect = effects.stream().anyMatch(effect -> ((IPersistentEffect) effect).mc_fundamental$persistsAfterDeath());
    if (!hasPersistentEffect) {
      return;
    }

    event.getToolTip().add(1, Component.translatable("item.fundamental.potion.persistent_tooltip").withStyle(ChatFormatting.GRAY));
  }

  public static void onPlayerDeath(LivingDeathEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    Collection<MobEffectInstance> activeEffects = player.getActiveEffects();

    if (activeEffects.isEmpty()) {
      return;
    }

    CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);

    ListTag effectsList = new ListTag();
    for (MobEffectInstance effect : activeEffects) {
      if (!((IPersistentEffect) effect).mc_fundamental$persistsAfterDeath()) {
        continue;
      }

      effectsList.add(effect.save(new CompoundTag()));
    }

    if (effectsList.isEmpty()) {
      return;
    }

    persistentData.put(TAG_EFFECTS_ON_DEATH, effectsList);

    player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);

    System.out.println(player.getPersistentData());
  }

  public static void onPlayerRespawn(PlayerEvent.Clone event) {
    if (!event.isWasDeath()) {
      return;
    }

    Player newPlayer = event.getEntity();
    Player oldPlayer = event.getOriginal();

    CompoundTag persistentData = oldPlayer.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);

    if (!persistentData.contains(TAG_EFFECTS_ON_DEATH)) {
      return;
    }

    MinecraftServer server = newPlayer.getServer();
    if (server != null) {
      // Delay the effect application to ensure it happens after all other respawn logic, and the client is aware of the new entity.
      server.tell(new TickTask(server.getTickCount(), () -> {
        ListTag effectsList = persistentData.getList(TAG_EFFECTS_ON_DEATH, Tag.TAG_COMPOUND);
        for (Tag tag : effectsList) {
          if (!(tag instanceof CompoundTag effectTag)) {
            continue;
          }

          MobEffectInstance effect = MobEffectInstance.load(effectTag);
          if (effect == null) {
            continue;
          }

          newPlayer.addEffect(effect);
        }

        persistentData.remove(TAG_EFFECTS_ON_DEATH);
      }));
    }
  }
}
