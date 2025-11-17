import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class WebServer {

    public static void main(String[] args) throws Exception {
        // Si querés que cree/actualice tablas automáticamente al arrancar:
        // DatabaseInit.main(null);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // sitio estático
        server.createContext("/", new StaticHandler("public"));

        // platos
        server.createContext("/api/platos/create", new PlatoCreateHandler());
        server.createContext("/api/platos/list",   new PlatoListHandler());

        // usuarios
        server.createContext("/api/usuarios/create", new UsuarioCreateHandler());
        server.createContext("/api/usuarios/list",   new UsuarioListHandler());

        server.setExecutor(null);
        System.out.println("Servidor en http://localhost:8080");
        server.start();
    }

    // ====== PLATOS ======
    static class PlatoCreateHandler implements HttpHandler {
        @Override public void handle(HttpExchange ex) throws IOException {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { sendText(ex, 405, "Method Not Allowed"); return; }
            Map<String, String> f = readForm(ex);
            String nombre = f.getOrDefault("nombre", "");
            double precio = parseDouble(f.get("precio"));
            CategoriaPlato categoria = CategoriaPlato.valueOf(f.getOrDefault("categoria", "PRINCIPAL"));
            boolean disponible = "on".equalsIgnoreCase(f.getOrDefault("disponible", ""));
            PlatoDao dao = new PlatoDao();
            try {
                dao.insertar(new Plato(nombre, precio, categoria, disponible));
                redirect(ex, "/?ok=plato");
            } catch (SQLException e) {
                sendText(ex, 500, e.getMessage());
            }
        }
    }
    static class PlatoListHandler implements HttpHandler {
        @Override public void handle(HttpExchange ex) throws IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { sendText(ex, 405, "Method Not Allowed"); return; }
            PlatoDao dao = new PlatoDao();
            try {
                var lista = dao.listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    var p = lista.get(i);
                    json.append("{\"id\":").append(p.getId())
                        .append(",\"nombre\":\"").append(esc(p.getNombre()))
                        .append("\",\"precio\":").append(p.getPrecio())
                        .append(",\"categoria\":\"").append(p.getCategoria().name())
                        .append("\",\"disponible\":").append(p.isDisponible())
                        .append("}");
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(ex, 200, json.toString());
            } catch (SQLException e) {
                sendText(ex, 500, e.getMessage());
            }
        }
    }

    // ====== USUARIOS ======
    static class UsuarioCreateHandler implements HttpHandler {
        @Override public void handle(HttpExchange ex) throws IOException {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { sendText(ex, 405, "Method Not Allowed"); return; }
            Map<String, String> f = readForm(ex);
            String nombre   = f.getOrDefault("nombre", "");
            int    edad     = parseInt(f.get("edad"));
            String username = f.getOrDefault("username", "");
            String password = f.getOrDefault("password", "");
            RolUsuario rol  = RolUsuario.valueOf(f.getOrDefault("rol", "EMPLEADO"));
            String puesto   = f.getOrDefault("puesto", "");
            double sueldo   = parseDouble(f.get("sueldo"));
            int    dias     = parseInt(f.get("dias"));
            double horas    = parseDouble(f.get("horas"));

            UsuarioDao dao = new UsuarioDao();
            try {
                dao.insertar(new Usuario(nombre, edad, username, password, rol, puesto, sueldo, dias, horas));
                redirect(ex, "/?ok=usuario");
            } catch (SQLException e) {
                sendText(ex, 500, e.getMessage());
            }
        }
    }
    static class UsuarioListHandler implements HttpHandler {
        @Override public void handle(HttpExchange ex) throws IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { sendText(ex, 405, "Method Not Allowed"); return; }
            UsuarioDao dao = new UsuarioDao();
            try {
                var lista = dao.listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    var u = lista.get(i);
                    json.append("{\"id\":").append(u.getId())
                        .append(",\"nombre\":\"").append(esc(u.getNombre()))
                        .append("\",\"edad\":").append(u.getEdad())
                        .append(",\"username\":\"").append(esc(u.getUsuario()))
                        .append("\",\"rol\":\"").append(u.getRol().name())
                        .append("\",\"puesto\":\"").append(esc(u.getPuesto()))
                        .append("\",\"sueldo\":").append(u.getSueldo())
                        .append(",\"dias\":").append(u.getDiasTrabajar())
                        .append(",\"horas\":").append(u.getHorasPorDia())
                        .append("}");
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                sendJson(ex, 200, json.toString());
            } catch (SQLException e) {
                sendText(ex, 500, e.getMessage());
            }
        }
    }

    // ====== estáticos ======
    static class StaticHandler implements HttpHandler {
        private final Path base;
        StaticHandler(String folder) { this.base = Path.of(folder); }
        @Override public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = base.resolve(path.substring(1)).normalize();
            if (!file.startsWith(base) || !Files.exists(file)) { sendText(ex, 404, "Not Found"); return; }
            String mime = path.endsWith(".css") ? "text/css" : path.endsWith(".html") ? "text/html" : "text/plain";
            byte[] bytes = Files.readAllBytes(file);
            ex.getResponseHeaders().set("Content-Type", mime + "; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        }
    }

    // ====== util ======
    static Map<String,String> readForm(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String,String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split("=", 2);
            String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String v = kv.length>1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(k, v);
        }
        return map;
    }
    static void sendText(HttpExchange ex, int code, String text) throws IOException {
        byte[] b = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }
    static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }
    static void redirect(HttpExchange ex, String location) throws IOException {
        ex.getResponseHeaders().set("Location", location);
        ex.sendResponseHeaders(303, -1);
        ex.close();
    }
    static double parseDouble(String s){ try { return Double.parseDouble(s); } catch(Exception e){ return 0; } }
    static int parseInt(String s){ try { return Integer.parseInt(s); } catch(Exception e){ return 0; } }
    static String esc(String s){ return s.replace("\"","\\\""); }
    
    
}
/*
- Servidor HTTP en :8080.
- Endpoints para crear/listar platos y usuarios (con los campos nuevos).
- Sirve archivos estáticos desde carpeta ./public.
*/
