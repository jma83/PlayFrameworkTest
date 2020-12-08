package controllers;

import models.Usuario;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.Optional;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;


//JSON con node.put
public class UsuarioController {

    public Result createUsuario(Http.Request request){
        JsonNode req = request.body().asJson();
        String nick = null;
        Integer age = null;
        if (req != null && req.has("nick"))
            nick = req.get("nick").asText();
        if (req != null && req.has("age"))
            age = req.get("age").asInt();

        ObjectNode node = Json.newObject();
        Result res = null;

        if (nick != null && !"".equals(nick)){
            if (comprobarDuplicidad(nick) == true) {

                node.put("success", false);
                node.put("message", "POST - Error, nick duplicado");
                res = (Result) Results.status(409,node);

            }
        }else{
            node.put("success", false);
            node.put("message", "POST - Error, nick vacio");
            res = (Result) Results.status(400,node);
        }
        if (res == null) {
            Usuario.nicks.add(nick);
            Usuario.ages.add(age);
            node.put("success", true);
            node.put("message", "POST - Ok. Se ha insertado el nick: " + nick + " y edad: " + age);
            res = Results.status(200, node);
        }


        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public Result getUsuario(Http.Request request){
        Result res = null;
        ObjectNode node = Json.newObject();

        if (Usuario.nicks.size() == 0) {
            node.put("success", false);
            node.put("message", "GET - Sin resultados");
            res = Results.notFound(node);
        }

        Optional<String> index = request.queryString("index");

        if (index.isPresent()){
            System.out.println(index.get());
            try {
                res= getConIndex(Integer.parseInt(index.get()));
            }catch (NumberFormatException e){
                System.err.println("Error formato no numerico");
                node.put("success", false);
                node.put("message", "Error formato no numerico");
                res = Results.badRequest(node);

            }
        }else{
            node.put("success", true);
            node.put("message", "GET Ok - " + Usuario.nicks + " " + Usuario.ages);
            res = Results.ok(node);
        }


        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));

    }

    public Result updateUsuario(Integer index,Http.Request request){
        Result res = null;
        ObjectNode node = Json.newObject();
        Integer age = null;
        String nick = null;

        if (index < 0 || Usuario.nicks.size() <= index){
            node.put("success", false);
            node.put("message", "PUT - Sin resultados");
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(index) == null) {
            node.put("success", false);
            node.put("message", "PUT - Sin resultados");
            res = Results.notFound(node);
        }

        JsonNode req = request.body().asJson();
        if (req != null && req.has("age"))
            age = req.get("age").asInt();

        if (req != null && req.has("nick"))
            nick = req.get("nick").asText();

        if (res == null) {
            String anteriorNick = Usuario.nicks.get(index);

            if (nick != null && !"".equals(nick)) {
                if (comprobarDuplicidad(nick) == true) {
                    node.put("success", false);
                    node.put("message", "POST - Error, nick duplicado");
                    res = (Result) Results.status(409, node);
                }
            }else{
                node.put("success", false);
                node.put("message", "PUT - Error, nick vacio");
                res = (Result) Results.status(400, node);
            }
            if (res == null) {
                Usuario.nicks.set(index,nick);
                Usuario.ages.set(index,age);
                node.put("success", true);
                node.put("message", "PUT - Ok. Se ha actualizado el nick de: "+ anteriorNick +" a "+ nick +" y la edad a: " + age);
                res = Results.status(200, node);
            }
        }

        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public Result deleteUsuario(Integer index){
        Result res = null;
        ObjectNode node = Json.newObject();

        if (index < 0 || Usuario.nicks.size() <= index){
            node.put("success", false);
            node.put("message", "DELETE - Sin resultados");
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(index) == null) {
            node.put("success", false);
            node.put("message", "DELETE - Sin resultados");
            res = Results.notFound(node);
        }


        if (res == null) {
            String s = Usuario.nicks.get(index);
            if (Usuario.nicks.remove(s)) {
                node.put("success", true);
                node.put("message", "DELETE Ok -" + s);
                res = Results.ok(node);
            }else{
                node.put("success", false);
                node.put("message", "DELETE Error al borrar -" + s);
                res = Results.badRequest(node);
            }
        }

        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public boolean comprobarDuplicidad (String nick){
        for (int i = 0; i < Usuario.nicks.size(); i++) {
            if (nick.equals(Usuario.nicks.get(i))) {
                return true;
            }
        }
        return false;
    }

    public Result getConIndex(int in){
        Result res = null;
        ObjectNode node = Json.newObject();
        if (in < 0 || Usuario.nicks.size() <= in){
            node.put("success", false);
            node.put("message", "GET - Sin resultados");
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(in) == null) {
            node.put("success", false);
            node.put("message", "GET - Sin resultados");
            res = Results.notFound(node);
        }

        if (res == null) {
            node.put("success", true);
            node.put("message", "GET Ok - " + Usuario.nicks.get(in) + " " +Usuario.ages.get(in));
            res = Results.ok(node);
        }

        return res;
    }
}

