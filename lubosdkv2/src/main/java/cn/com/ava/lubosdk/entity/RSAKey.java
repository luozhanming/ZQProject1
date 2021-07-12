package cn.com.ava.lubosdk.entity;

public class RSAKey implements QueryResult{
    private String modulus;
    private String exponent;

    public String getModulus() {
        return modulus;
    }

    public void setModulus(String modulus) {
        this.modulus = modulus;
    }

    public String getExponent() {
        return exponent;
    }

    public void setExponent(String exponent) {
        this.exponent = exponent;
    }

    @Override
    public String toString() {
        return "RSAKey{" +
                "modulus='" + modulus + '\'' +
                ", exponent='" + exponent + '\'' +
                '}';
    }
}
