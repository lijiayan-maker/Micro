package com.mycro.micro.test;

/**
 * Author: canyan.zhang
 * Email:canyan.zhang@xjmz.com
 * Date: 2025/5/10 18:27
 * Description: YEAR!!
 */
public class FactoryPattern {
    public static void main(String[] args){

    }
}



class HuaWeiPhone implements Phone{

}

interface Factory{
    Phone createProduct();
}

class IPhoneFactory implements Factory{

    @Override
    public Phone createProduct() {
        return new Iphone();
    }
}

class HuaiWeiFactory implements Factory{

    @Override
    public Phone createProduct() {
        return new HuaWeiPhone();
    }
}


//抽象工厂
interface AbstractFactory {
    Phone createPhone();
    Mask createMask();
}

//具体工厂
class SuperFactory implements AbstractFactory{

    @Override
    public Phone createPhone() {
        return new Iphone();
    }

    @Override
    public Mask createMask() {
        return new N95();
    }
}


//产品大类-手机
interface Phone{ }
class Iphone implements Phone{
}

//产品大类-口罩
interface Mask{ }

class N95 implements Mask{
}
