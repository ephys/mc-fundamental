package be.ephys.fundamental.potion;

public interface IPersistentEffect {
  boolean mc_fundamental$persistsAfterDeath();

  void mc_fundamental$setPersistsAfterDeath(boolean persist);
}
