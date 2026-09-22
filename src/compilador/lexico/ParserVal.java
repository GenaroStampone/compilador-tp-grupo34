package lexico;

public class ParserVal {
    public int ival;
    public double dval;
    public String sval;
    public Object obj;

    public ParserVal() {}
    public ParserVal(int val) { this.ival = val; }
    public ParserVal(double val) { this.dval = val; }
    public ParserVal(String val) { this.sval = val; }
    public ParserVal(Object val) { this.obj = val; }
}