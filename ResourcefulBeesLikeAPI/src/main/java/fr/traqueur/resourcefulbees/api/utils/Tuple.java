package fr.traqueur.resourcefulbees.api.utils;

public class Tuple<A, B> {

    private A a;
    private B b;

    public Tuple(A left, B right) {
        this.a = left;
        this.b = right;
    }

    public A getLeft() {
        return this.a;
    }

    public void setLeft(A left) {
        this.a = left;
    }

    public B getRight() {
        return this.b;
    }

    public void setRight(B right) {
        this.b = right;
    }
}
