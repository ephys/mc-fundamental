package be.ephys.fundamental.utils;

import java.util.Random;

public class MathUtils {
  public static int randomIntInclusive(Random random, int min, int max) {
    return random.nextInt(max - min + 1) + min;
  }
}
