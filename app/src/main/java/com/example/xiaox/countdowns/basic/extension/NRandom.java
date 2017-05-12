package com.example.xiaox.countdowns.basic.extension;

import java.util.Random;

/**
 * Created by xiaox on 2/19/2017.
 */
public class NRandom {

    public static int[] getSequence(int size){
        int[] sequence = new int[size];
        for(int i = 0; i < size; i++){
            sequence[i] = i;
        }
        Random random = new Random();
        for(int i = 0; i < size; i++){
            int p = random.nextInt(size);
            int tmp = sequence[i];
            sequence[i] = sequence[p];
            sequence[p] = tmp;
        }
        random = null;
        return sequence;
    }

    public static int[] getSequence2(int[] arr){
        int[] arr2 = new int[arr.length];
        int count = arr.length;
        int cbRandCount = 0;
        int cbPosition = 0;
        int k = 0;
        do{
            Random rand = new Random();
            int r = count - cbRandCount;
            cbPosition = rand.nextInt(r);
            arr2[k++] = arr[cbPosition];
            cbRandCount++;
            arr[cbPosition] = arr[r - 1];
        }while(cbRandCount < count);
        return arr2;
    }
}
