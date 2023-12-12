package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {


    public MinHeapImpl(){
        this.elements = (E[]) new Comparable[10];
    }


    //after you add an element it goes to bottom of heap/heap array,
    //reHeap then reorders based on priority using comparable looking
    //at last time used and stuff
    public void reHeapify(E element){
        int elementIndex = getArrayIndex(element);
        if (elementIndex == -1) {
            // element is not in the heap
            throw new NoSuchElementException();
        }
        upHeap(elementIndex);
        downHeap(elementIndex);
    }

    protected int getArrayIndex(E element){
        //boolean found = false;
        //changed count to elements.length
        for (int i = 0; i < this.elements.length; i++) {
            if (elements[i] == null){
                continue;
            }
            if (elements[i].equals(element)) {
                //    found = true;
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    protected void doubleArraySize(){
        int doubleSize = elements.length * 2;
        E[] doubledArray = (E[]) new Comparable[doubleSize];
        for (int i=1; i< elements.length; i++){
            doubledArray[i] = elements[i];
        }
        elements = doubledArray;
    }



}

