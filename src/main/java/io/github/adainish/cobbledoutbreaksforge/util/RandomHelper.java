package io.github.adainish.cobbledoutbreaksforge.util;

import com.google.common.collect.Sets;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RandomHelper
{
    @Deprecated
    public static final Random rand = new Random();

    public RandomHelper() {
    }

    public static <T> T getRandomElementExcluding(T[] elements, T... excluded) {
        if (elements.length <= excluded.length && Arrays.asList(excluded).containsAll(Arrays.asList(elements))) {
            return null;
        } else {
            Set<T> excludedSet = Sets.newHashSet(excluded);

            Object element;
            do {
                element = getRandomElementFromArray(elements);
            } while(excludedSet.contains(element));

            return (T) element;
        }
    }

    public static int getRandomNumberBetween(int min, int max) {
        return useRandomForNumberBetween(rand, min, max);
    }

    public static float getRandomNumberBetween(float min, float max) {
        return useRandomForNumberBetween(rand, min, max);
    }

    public static int[] getRandomDistinctNumbersBetween(int min, int max, int numElements) {
        int totalNumbers = max - min + 1;
        if (numElements < 1) {
            return new int[0];
        } else {
            if (numElements > totalNumbers) {
                numElements = totalNumbers;
            }

            int[] randomNumbers = new int[numElements];
            List<Integer> allNumbers = new ArrayList(totalNumbers);

            int i;
            for(i = 0; i < totalNumbers; ++i) {
                allNumbers.add(min + i);
            }

            for(i = 0; i < numElements; ++i) {
                randomNumbers[i] = (Integer)getRandomElementFromList((List)allNumbers);
                allNumbers.remove(randomNumbers[i]);
            }

            return randomNumbers;
        }
    }

    public static int useRandomForNumberBetween(Random random, int min, int max) {
        return random.nextInt(Math.max(1, max - min + 1)) + min;
    }

    public static float useRandomForNumberBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    /** @deprecated */
    public static <T> T getRandomElementFromList(List<T> list) {
        return getRandomElementFromCollection(list);
    }

    public static <T> T getRandomElementFromList(Collection<T> collection) {
        if (!collection.isEmpty()) {
            int index = getRandomNumberBetween(0, collection.size() - 1);
            int counter = 0;

            for (Iterator var3 = collection.iterator(); var3.hasNext(); ++counter) {
                T t = (T) var3.next();
                if (counter == index) {
                    return t;
                }
            }

        }
        return null;
    }

    public static <T> T getRandomElementFromArray(T... array) {
        return array != null && array.length != 0 ? array[getRandomNumberBetween(0, array.length - 1)] : null;
    }

    public static <T> T removeRandomElementFromList(List<T> list) {
        return !list.isEmpty() ? list.remove(getRandomNumberBetween(0, list.size() - 1)) : null;
    }

    public static boolean getRandomChance(double chance) {
        return rand.nextDouble() < chance;
    }

    public static boolean getRandomChance(float chance) {
        return rand.nextFloat() < chance;
    }

    public static boolean getRandomChance(int chance) {
        return getRandomChance((float)chance / 100.0F);
    }

    public static boolean getRandomChance() {
        return getRandomChance(0.5F);
    }

    public static boolean getRandomChance(Random random, int chance) {
        return random.nextFloat() < (float)chance / 100.0F;
    }

    public static int getFortuneAmount(int fortune) {
        return fortune > 0 ? Math.max(1, rand.nextInt(fortune + 2)) : 1;
    }

    public static int getRandomIndexFromWeights(List<Integer> weights) {
        int totalWeight = 0;

        Integer weight;
        for(Iterator var2 = weights.iterator(); var2.hasNext(); totalWeight += weight) {
            weight = (Integer)var2.next();
        }

        if (totalWeight > 0) {
            int num = getRandomNumberBetween(0, totalWeight - 1);
            int sum = 0;

            for(int i = 0; i < weights.size(); ++i) {
                sum += (Integer)weights.get(i);
                if (num < sum) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static Color getRandomHighSaturationColor() {
        return Color.getHSBColor(rand.nextFloat() * 360.0F, 1.0F, 1.0F);
    }

    public static <T> T getRandomElementFromCollection(Collection<T> collection) {
        if (collection.isEmpty()) {
            return null;
        } else {
            int index = rand.nextInt(collection.size());
            Iterator<T> iterator = collection.iterator();

            for(int i = 0; i < index; ++i) {
                iterator.next();
            }

            return iterator.next();
        }
    }

    public static <T> T removeRandomElementFromCollection(Collection<T> collection) {
        T element = getRandomElementFromCollection(collection);
        if (element != null) {
            collection.remove(element);
            return element;
        } else {
            return null;
        }
    }

    public static Random getRandom() {
        return rand;
    }
}
