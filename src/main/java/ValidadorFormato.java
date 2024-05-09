import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorFormato {
    private Pattern patron;
    private Matcher comprobador;
    public ValidadorFormato(){
        this.patron=Pattern.compile("([a-z|A-Z]|[0-9]){1,}\\.[a-z|A-Z]{3}");//patron  \\w+\\.[a-zA-Z]
        comprobador=null;

    }
    public boolean formatoCorrecto(String datos){
        comprobador= patron.matcher(datos);
        if(comprobador.matches()) return true;
        return false;
    }
}
