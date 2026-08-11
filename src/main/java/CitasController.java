import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import jpa.entities.Citas; // Asegúrate de que este sea el paquete correcto de tu entidad

/**
 * @author User
 */
@Named("citasBean")
@ViewScoped
public class CitasController implements Serializable {

    private Citas cita = new Citas();

    // Constructor vacío
    public CitasController() {
    }

    public void guardar() {
        // Aquí iría la lógica para persistir tu objeto 'cita'
        // Por ejemplo: service.create(cita);
        System.out.println("Guardando cita: " + cita.toString());
    }

    // Getters y Setters
    public Citas getCita() {
        return cita;
    }

    public void setCita(Citas cita) {
        this.cita = cita;
    }
}