import java.time.LocalTime;
import java.util.List;


public interface IOperation {
    String getId();
    String getDescription();
    LocalTime getNominalTime();
    List<AGV> getResources();

    void setData(Object data);
    Object getData(String dataType);
    LocalTime getDuration();
}