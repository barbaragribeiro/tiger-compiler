package Semant;

import Types.*;

public class Entry {
    Type typ;
    int level;

    public Entry(Type ty, int lvl) {
        typ = ty;
        level = lvl;
    }

    public Entry(Type ty) {
        typ = ty;
    }
}
