package com.example.parcial_2_am_acn4av_gonzales_oturakdjian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiscountHelper {
    private static final double SENIOR_DISCOUNT_PERCENTAGE = 0.15; // 15%
    private static final double PAYMENT_METHOD_DISCOUNT_PERCENTAGE = 0.25; // 25%
    private static final int WOMAN_AGE_THRESHOLD = 60;
    private static final int MAN_AGE_THRESHOLD = 65;

    /**
     * Calculates if a user qualifies for senior discount
     * @param birthDateString Birth date in format "dd/MM/yyyy"
     * @param gender "Mujer" or "Hombre"
     * @return true if user qualifies for discount
     */
    public static boolean qualifiesForDiscount(String birthDateString, String gender) {
        if (birthDateString == null || birthDateString.isEmpty() || 
            gender == null || gender.isEmpty()) {
            return false;
        }

        int age = calculateAge(birthDateString);
        if (age < 0) {
            return false; // Invalid date
        }

        if (gender.equalsIgnoreCase("Mujer") || gender.equalsIgnoreCase("Femenino") || 
            gender.equalsIgnoreCase("F")) {
            return age > WOMAN_AGE_THRESHOLD;
        } else if (gender.equalsIgnoreCase("Hombre") || gender.equalsIgnoreCase("Masculino") || 
                   gender.equalsIgnoreCase("M")) {
            return age > MAN_AGE_THRESHOLD;
        }

        return false;
    }

    /**
     * Calculates age from birth date string
     * @param birthDateString Date in format "dd/MM/yyyy"
     * @return age in years, or -1 if invalid
     */
    public static int calculateAge(String birthDateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date birthDate = sdf.parse(birthDateString);
            if (birthDate == null) {
                return -1;
            }

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthDate);
            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            
            // Check if birthday hasn't occurred this year
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            android.util.Log.e("DiscountHelper", "Error parsing birth date: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Applies discount to a total amount
     * @param total Original total
     * @param qualifiesForDiscount Whether user qualifies
     * @return Discounted total
     */
    public static double applyDiscount(double total, boolean qualifiesForDiscount) {
        if (qualifiesForDiscount) {
            return total * (1 - SENIOR_DISCOUNT_PERCENTAGE);
        }
        return total;
    }

    /**
     * Gets the discount amount
     * @param total Original total
     * @param qualifiesForDiscount Whether user qualifies
     * @return Discount amount
     */
    public static double getDiscountAmount(double total, boolean qualifiesForDiscount) {
        if (qualifiesForDiscount) {
            return total * SENIOR_DISCOUNT_PERCENTAGE;
        }
        return 0.0;
    }

    /**
     * Checks if a payment method qualifies for discount
     * @param paymentMethod PaymentMethod object
     * @return true if payment method has discount
     */
    public static boolean paymentMethodHasDiscount(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        
        String type = paymentMethod.getType();
        String brand = paymentMethod.getCardBrand();
        
        // Mercado Pago has 25% discount
        if (PaymentMethod.TYPE_MERCADO_PAGO.equals(type)) {
            return true;
        }
        
        // Naranja X has 25% discount
        if (brand != null && brand.equalsIgnoreCase("Naranja X")) {
            return true;
        }
        
        return false;
    }

    /**
     * Applies payment method discount to total
     * @param total Original total
     * @param paymentMethod PaymentMethod object
     * @return Discounted total
     */
    public static double applyPaymentMethodDiscount(double total, PaymentMethod paymentMethod) {
        if (paymentMethodHasDiscount(paymentMethod)) {
            return total * (1 - PAYMENT_METHOD_DISCOUNT_PERCENTAGE);
        }
        return total;
    }

    /**
     * Gets payment method discount amount
     * @param total Original total
     * @param paymentMethod PaymentMethod object
     * @return Discount amount
     */
    public static double getPaymentMethodDiscountAmount(double total, PaymentMethod paymentMethod) {
        if (paymentMethodHasDiscount(paymentMethod)) {
            return total * PAYMENT_METHOD_DISCOUNT_PERCENTAGE;
        }
        return 0.0;
    }

    /**
     * Checks if payment method supports installments
     * @param paymentMethod PaymentMethod object
     * @return true if supports installments
     */
    public static boolean supportsInstallments(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        
        // Visa credit cards support 6 installments with no interest
        return PaymentMethod.TYPE_CREDIT.equals(paymentMethod.getType()) &&
               "Visa".equalsIgnoreCase(paymentMethod.getCardBrand());
    }

    /**
     * Applies both senior discount and payment method discount
     * @param total Original total
     * @param qualifiesForSeniorDiscount Whether user qualifies for senior discount
     * @param paymentMethod PaymentMethod object
     * @return Final discounted total
     */
    public static double applyAllDiscounts(double total, boolean qualifiesForSeniorDiscount, PaymentMethod paymentMethod) {
        double discountedTotal = total;
        
        // Apply senior discount first
        if (qualifiesForSeniorDiscount) {
            discountedTotal = applyDiscount(discountedTotal, true);
        }
        
        // Apply payment method discount
        if (paymentMethodHasDiscount(paymentMethod)) {
            discountedTotal = applyPaymentMethodDiscount(discountedTotal, paymentMethod);
        }
        
        return discountedTotal;
    }

    /**
     * Gets total discount amount (senior + payment method)
     * @param total Original total
     * @param qualifiesForSeniorDiscount Whether user qualifies for senior discount
     * @param paymentMethod PaymentMethod object
     * @return Total discount amount
     */
    public static double getTotalDiscountAmount(double total, boolean qualifiesForSeniorDiscount, PaymentMethod paymentMethod) {
        double seniorDiscount = qualifiesForSeniorDiscount ? getDiscountAmount(total, true) : 0.0;
        double totalAfterSenior = total - seniorDiscount;
        double paymentDiscount = getPaymentMethodDiscountAmount(totalAfterSenior, paymentMethod);
        return seniorDiscount + paymentDiscount;
    }
}

