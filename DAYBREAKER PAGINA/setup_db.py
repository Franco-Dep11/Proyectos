# setup_db.py
import sqlite3

# Conectar (o crear) la base de datos
conn = sqlite3.connect('productos.db')
cursor = conn.cursor()

# Crear tabla de productos (descripcion es opcional => sin NOT NULL)
cursor.execute('''
    CREATE TABLE IF NOT EXISTS productos (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre TEXT NOT NULL,
        descripcion TEXT,
        imagen TEXT
    )
''')

# Crear tabla de fichas técnicas
cursor.execute('''
    CREATE TABLE IF NOT EXISTS fichas_tecnicas (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        producto_id INTEGER,
        clave TEXT NOT NULL,
        valor TEXT NOT NULL,
        FOREIGN KEY (producto_id) REFERENCES productos(id)
    )
''')

# ELIMINAR registros previos para evitar duplicados
cursor.execute("DELETE FROM fichas_tecnicas")
cursor.execute("DELETE FROM productos")

# Insertar productos de ejemplo
productos_iniciales = [
    ("Contenedor de basura 120 Lts", "Contenedor de 120 litros con tapa y ruedas", "image1.png"),
    ("Contenedor de basura 240 Lts", "Contenedor de 240 litros con tapa y ruedas", "image2.png"),
    ("Polietileno Negro 2 Metros", "Rollo de polietileno negro de 2 metros", "image3.png"),
    ("Polietileno Negro 3 Metros", "Rollo de polietileno negro de 3 metros", "image3.png"),
    ("Polietileno Negro 4 Metros", "Rollo de polietileno negro de 4 metros", "image3.png")
]

cursor.executemany("""
    INSERT INTO productos (nombre, descripcion, imagen)
    VALUES (?, ?, ?)
""", productos_iniciales)

# Obtener los IDs de los productos recién insertados
cursor.execute("SELECT id FROM productos WHERE nombre = 'Contenedor de basura 120 Lts'")
producto1_id = cursor.fetchone()[0]

cursor.execute("SELECT id FROM productos WHERE nombre = 'Contenedor de basura 240 Lts'")
producto2_id = cursor.fetchone()[0]

cursor.execute("SELECT id FROM productos WHERE nombre = 'Polietileno Negro 2 Metros'")
producto3_id = cursor.fetchone()[0]

cursor.execute("SELECT id FROM productos WHERE nombre = 'Polietileno Negro 3 Metros'")
producto4_id = cursor.fetchone()[0]

cursor.execute("SELECT id FROM productos WHERE nombre = 'Polietileno Negro 4 Metros'")
producto5_id = cursor.fetchone()[0]

# Insertar fichas técnicas
fichas = [
    (producto1_id, "Alto", "92,6 cm"),
    (producto1_id, "Ancho", "43,7 cm"),
    (producto1_id, "Carga Útil", "48 kg"),
    (producto2_id, "Alto", "105,5 cm"),
    (producto2_id, "Ancho", "54,5 cm"),
    (producto2_id, "Carga Útil", "96 kg"),
    (producto3_id, "Ancho", "2 metros"),
    (producto3_id, "Espesor", "100 y 200 micrones"),
    (producto4_id, "Ancho", "3 metros"),
    (producto4_id, "Espesor", "100 y 200 micrones"),
    (producto5_id, "Ancho", "4 metros"),
    (producto5_id, "Espesor", "100 y 200 micrones")
]

cursor.executemany("""
    INSERT INTO fichas_tecnicas (producto_id, clave, valor) 
    VALUES (?, ?, ?)
""", fichas)

conn.commit()
conn.close()

print("Base de datos creada o actualizada con éxito (sin duplicados).")
