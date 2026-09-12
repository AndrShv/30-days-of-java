package backend.patterns;

import lombok.*;

public class Strategy {
    Payment card_payment = new CashPayment();
    Payment cash_payment = new CashPayment();

    public static void main(String[] args) {
        Strategy context = new Strategy();
        double amountToPay = 100.50;
        System.out.println("--- Вызов разных стратегий оплаты ---");
        context.card_payment.pay(amountToPay);
        context.cash_payment.pay(amountToPay);


    }




}


abstract class Payment{
    void pay(double amount) {
        System.out.println("Payment of " + amount + " made");
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
class CardPayment extends Payment{

    @Override
    public void pay(double amount) {
        System.out.println("Card payment of " + amount + " made");
    }
}
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
class CashPayment extends Payment{

    @Override
    public void pay(double amount) {
        System.out.println("Cash payment of " + amount + " made");

    }
}
