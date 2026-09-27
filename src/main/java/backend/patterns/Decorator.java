package backend.patterns;


import lombok.Data;
import lombok.EqualsAndHashCode;

public class Decorator {
    static Coffee milkCoffee = new MilkCoffee();
    static Coffee chocolateCoffee = new ChocolateCoffee();
    public static void main(String[] args) {
        System.out.println(milkCoffee.getCoffeePrice());
        System.out.println(chocolateCoffee.getCoffeePrice());
    }
}



@Data
class Coffee{
    public double price = 100;
    public double getCoffeePrice(){
        return price;
    }
}

@Data
@EqualsAndHashCode(callSuper = true)
class MilkCoffee extends Coffee{
    @Override
    public double getCoffeePrice(){
        return super.getPrice()*1.2;
    }
}
@Data
@EqualsAndHashCode(callSuper = true)
class ChocolateCoffee extends Coffee{
    @Override
    public double getCoffeePrice(){
        return super.getPrice()*1.5;
    }
}
