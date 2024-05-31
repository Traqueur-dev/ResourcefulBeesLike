package fr.traqueur.ressourcefulbees.api;

public class Tuple<A, B> {

    private A a;
    private B b;

    public Tuple(A left, B right) {
        this.a = left;
        this.b = right;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A left) {
        this.a = left;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B right) {
        this.b = right;
    }
}
