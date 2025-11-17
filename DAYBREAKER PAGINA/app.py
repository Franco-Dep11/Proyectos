# app.py
import os
import sqlite3
from flask import Flask, render_template, request, redirect, url_for, session, jsonify
from werkzeug.utils import secure_filename

app = Flask(__name__)

# Usar variable de entorno para la clave secreta; si no está definida, se usa una por defecto (no recomendable en producción)
app.secret_key = os.environ.get("SECRET_KEY", "clave_por_defecto")

# Carpeta donde se guardarán las imágenes subidas
UPLOAD_FOLDER = "static"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def conectar_bd():
    return sqlite3.connect("productos.db")

# Obtener todos los productos
def obtener_productos():
    conn = conectar_bd()
    cursor = conn.cursor()
    cursor.execute("SELECT id, nombre, descripcion, imagen FROM productos")
    rows = cursor.fetchall()
    conn.close()

    productos = []
    for r in rows:
        productos.append({
            "id": r[0],
            "nombre": r[1],
            "descripcion": r[2],
            "imagen": r[3]
        })
    return productos

# Obtener un producto y su ficha técnica
def obtener_producto(id):
    conn = conectar_bd()
    cursor = conn.cursor()

    cursor.execute("SELECT nombre, descripcion, imagen FROM productos WHERE id = ?", (id,))
    prod_row = cursor.fetchone()
    if not prod_row:
        conn.close()
        return None

    cursor.execute("SELECT clave, valor FROM fichas_tecnicas WHERE producto_id = ?", (id,))
    ficha_rows = cursor.fetchall()
    conn.close()

    ficha_dict = {}
    for f in ficha_rows:
        ficha_dict[f[0]] = f[1]

    producto = {
        "id": id,
        "nombre": prod_row[0],
        "descripcion": prod_row[1],
        "imagen": prod_row[2],
        "ficha": ficha_dict
    }
    return producto

# --- RUTAS ---

# 1) Ruta raíz: Presentación
@app.route('/')
def presentacion():
    return render_template("presentacion.html")

# 2) Ruta para la lista de productos
@app.route('/productos')
def lista_productos():
    productos = obtener_productos()
    return render_template("index.html", productos=productos)

# 3) Ruta para ver detalle de un producto
@app.route('/producto/<int:id>')
def producto(id):
    p = obtener_producto(id)
    if not p:
        return "<h1>Producto no encontrado</h1>", 404
    return render_template("producto.html", producto=p)

# 4) Ruta para login
@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        usuario = request.form.get("username")
        password = request.form.get("password")
        # Credenciales actualizadas
        if usuario == "FRANCO" and password == "1franco7":
            session['admin'] = True
            return redirect(url_for('admin_panel'))
        else:
            return "<h1>Credenciales incorrectas</h1>"
    return render_template('login.html')

# 5) Ruta para cerrar sesión
@app.route('/logout')
def logout():
    session.pop('admin', None)
    return redirect(url_for('presentacion'))

# 6) Ruta para panel de administración
@app.route('/admin')
def admin_panel():
    if 'admin' not in session:
        return redirect(url_for('login'))
    productos = obtener_productos()
    return render_template("admin.html", productos=productos)

# 7) Ruta para agregar o editar un producto
@app.route('/admin/producto', methods=['POST'])
def agregar_editar_producto():
    if 'admin' not in session:
        return jsonify({"error": "Acceso denegado"}), 403

    id_producto = request.form.get("id")
    nombre = request.form.get("nombre", "")
    descripcion = request.form.get("descripcion", "")
    imagen_file = request.files.get("imagen")
    filename = ""

    if imagen_file:
        filename = secure_filename(imagen_file.filename)
        ruta_imagen = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        imagen_file.save(ruta_imagen)

    conn = conectar_bd()
    cursor = conn.cursor()

    if id_producto:
        if filename:
            cursor.execute("""
                UPDATE productos
                SET nombre = ?, descripcion = ?, imagen = ?
                WHERE id = ?
            """, (nombre, descripcion, filename, id_producto))
        else:
            cursor.execute("""
                UPDATE productos
                SET nombre = ?, descripcion = ?
                WHERE id = ?
            """, (nombre, descripcion, id_producto))
    else:
        cursor.execute("""
            INSERT INTO productos (nombre, descripcion, imagen)
            VALUES (?, ?, ?)
        """, (nombre, descripcion, filename))

    conn.commit()
    conn.close()
    return redirect(url_for('admin_panel'))

# 8) Ruta para eliminar un producto (y sus fichas técnicas asociadas)
@app.route('/admin/producto/<int:id>', methods=['DELETE'])
def eliminar_producto(id):
    if 'admin' not in session:
        return jsonify({"error": "Acceso denegado"}), 403

    conn = conectar_bd()
    cursor = conn.cursor()
    cursor.execute("DELETE FROM fichas_tecnicas WHERE producto_id = ?", (id,))
    cursor.execute("DELETE FROM productos WHERE id = ?", (id,))
    conn.commit()
    conn.close()

    return jsonify({"mensaje": "Producto eliminado"}), 200

if __name__ == '__main__':
    # Modo debug desactivado para producción (puedes activarlo si necesitas depurar)
    app.run(debug=False)
