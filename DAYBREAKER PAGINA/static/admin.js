// static/admin.js
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("producto-form");
  
    form.addEventListener("submit", async function (event) {
      event.preventDefault();
  
      let formData = new FormData();
      formData.append("id", document.getElementById("producto-id").value);
      formData.append("nombre", document.getElementById("nombre").value);
      formData.append("descripcion", document.getElementById("descripcion").value);
      
      let imagenFile = document.getElementById("imagen").files[0];
      if (imagenFile) {
        formData.append("imagen", imagenFile);
      }
  
      let response = await fetch("/admin/producto", {
        method: "POST",
        body: formData
      });
  
      if (response.ok) {
        window.location.reload();
      } else {
        alert("Ocurrió un error al guardar el producto.");
      }
    });
  });
  
  function editarProducto(id, nombre, descripcion) {
    document.getElementById("producto-id").value = id;
    document.getElementById("nombre").value = nombre;
    document.getElementById("descripcion").value = descripcion;
    // La imagen no puede prellenarse por seguridad del navegador.
  }
  
  async function eliminarProducto(id) {
    let confirmacion = confirm("¿Seguro que quieres eliminar este producto?");
    if (confirmacion) {
      let response = await fetch(`/admin/producto/${id}`, { method: "DELETE" });
      if (response.ok) {
        window.location.reload();
      } else {
        alert("No se pudo eliminar el producto.");
      }
    }
  }
  