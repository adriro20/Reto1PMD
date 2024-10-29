import java.util.Map;
import java.util.HashMap;

public interface DBAccesible {

    public Map<String, Ejercicio> selectEjercicio();

    public void insertEjercicio(Ejercicio ejer);

}
