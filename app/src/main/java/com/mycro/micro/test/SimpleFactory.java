package com.mycro.micro.test;

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/5/10 15:24
 * Description: YEAR!!
 */
public class SimpleFactory {

    public static Product createProduct(String type){
        if (type.equals("A")){
            return new ProductA();
        }else {
            return new ProductB();
        }
    }
}

abstract class Product {

}

class ProductA extends Product{

}

class ProductB extends Product{

}
