    package modelo;

    public class Usuario {
        private int idUsuario;
        private String nombreUsuario;
        private String rol;

        // Constructor vacío
        public Usuario() {}

        // Constructor con datos
        public Usuario(int idUsuario, String nombreUsuario, String rol) {
            this.idUsuario = idUsuario;
            this.nombreUsuario = nombreUsuario;
            this.rol = rol;
        }

        // Getters y Setters
        public int getIdUsuario() { return idUsuario; }
        public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }