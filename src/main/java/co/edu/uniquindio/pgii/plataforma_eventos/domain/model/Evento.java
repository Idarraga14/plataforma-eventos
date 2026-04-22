package co.edu.uniquindio.pgii.plataforma_eventos.domain.model;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoCategoria;
import co.edu.uniquindio.pgii.plataforma_eventos.domain.enums.EventoEstado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Evento {
    private final String idEvento;
    private final String nombre;
    private final EventoCategoria categoria;
    private final String descripcion;
    private final String ciudad;
    private final LocalDateTime fecha;
    private final EventoEstado estado;
    private final List<String> politicas;
    private final Recinto recinto;

    private Evento(EventoBuilder builder) {
        this.idEvento = UUID.randomUUID().toString();
        this.nombre = builder.nombre;
        this.categoria = builder.categoria;
        this.descripcion = builder.descripcion;
        this.ciudad = builder.ciudad;
        this.fecha = builder.fecha;
        this.recinto = builder.recinto;
        this.estado = EventoEstado.BORRADOR;
        this.politicas = new ArrayList<>();
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public EventoCategoria getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EventoEstado getEstado() {
        return estado;
    }

    public List<String> getPoliticas() {
        return politicas;
    }

    public Recinto getRecinto() {
        return recinto;
    }

    public static class EventoBuilder {
        private String nombre;
        private EventoCategoria categoria;
        private String descripcion;
        private String ciudad;
        private LocalDateTime fecha;
        private Recinto recinto;
        private List<String> politicas = new ArrayList<>();

        // Métodos "Fluent"
        public EventoBuilder conNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public EventoBuilder deCategoria(EventoCategoria categoria) {
            this.categoria = categoria;
            return this;
        }

        public EventoBuilder conDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public EventoBuilder enCiudad(String ciudad) {
            this.ciudad = ciudad;
            return this;
        }

        public EventoBuilder paraLaFecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public EventoBuilder enRecinto(Recinto recinto) {
            this.recinto = recinto;
            return this;
        }

        public EventoBuilder agregandoPolitica(String politica) {
            this.politicas.add(politica);
            return this;
        }

        // 3. LA VALIDACIÓN (El escudo de la arquitectura)
        public Evento build() {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalStateException("El evento debe tener un nombre.");
            }
            if (fecha == null || fecha.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("El evento debe tener una fecha válida en el futuro.");
            }
            if (recinto == null) {
                throw new IllegalStateException("El evento no puede existir sin un recinto físico asignado.");
            }
            if (categoria == null) {
                throw new IllegalStateException("Debe especificar la categoría del evento.");
            }

            return new Evento(this);
        }
    }
}
