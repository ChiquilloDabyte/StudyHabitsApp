-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    correo TEXT NOT NULL,
    contrasena TEXT NOT NULL
);

-- Tabla de tareas
CREATE TABLE IF NOT EXISTS Tareas (
    idTarea INTEGER PRIMARY KEY AUTOINCREMENT,
    idUsuario INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    completada BOOLEAN DEFAULT 0,
    fechaEntrega TEXT,
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario)
);


