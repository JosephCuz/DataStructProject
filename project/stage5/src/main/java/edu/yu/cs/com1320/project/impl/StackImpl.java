package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;


public class StackImpl<T> implements Stack<T>{

    int top;
    item<T>[] stackArray = new item[10];

    public StackImpl(){
        this.top = -1;
    }

    private class item<T>{
        T content;
        private item(T object){
            this.content = object;
        }
        private T getContent(){
            return this.content;
        }
    }

    /**
     * @param element object to add to the Stack
     */
    public void push(T element){
        if (top == stackArray.length-2){
            item<T>[] tempArray = stackArray;
            stackArray = new item[stackArray.length*2];
            for (int i=0; i < tempArray.length; i++){
                stackArray[i]= tempArray[i];
            }
        }
        if (element != null){
            item<T> newItem = new item<>(element);
            top++;
            stackArray[top] = newItem;
        }
        else {return;}
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack,
     * null if the stack is empty
     */
    public T pop(){
        if (this.top == -1){
            return null;
        }
        else{
            T popped = stackArray[this.top].getContent();
            stackArray[this.top] = null;
            this.top = this.top-1;
            //System.out.println("pop's top: "+this.top);
            //System.out.println("pop's size: "+this.size());
            return popped;
        }
    }

    /**
     *
     * @return the element at the top
     * of the stack without removing it
     */
    public T peek(){
        if (this.top == -1){
            return null;
        }
        else {
            T peeked = stackArray[this.top].getContent();
            return peeked;
        }
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size(){
        int sizeInt = this.top+1;
        return sizeInt;
    }
    /**
     public static void main(String[] args){
     StackImpl<String> test = new StackImpl<>();

     test.push("a");
     test.push("b");
     test.push("c");
     test.push("d");
     test.push("e");
     System.out.println(test.size());
     System.out.println(test.stackArray.length);
     test.push("a");
     test.push("b");
     test.push("c");
     test.push("d");
     test.push("e");
     System.out.println(test.size());
     System.out.println(test.stackArray.length);
     test.push("a");
     test.push("b");
     test.push("c");
     test.push("d");
     test.push("e");
     System.out.println(test.size());
     System.out.println(test.stackArray.length);

     System.out.println(test.peek());
     System.out.println(test.pop());
     System.out.println(test.peek());
     System.out.println(test.pop());
     System.out.println(test.peek());
     System.out.println(test.pop());
     System.out.println(test.peek());
     System.out.println(test.pop());
     System.out.println(test.peek());
     System.out.println(test.pop());
     System.out.println(test.size());

     //    for (int i=0; i < test.size(); i++){
     //        System.out.println(test.pop());
     //    }
     }
     */

}

