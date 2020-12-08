package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Usuario;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.Optional;

//JSON con RespuestaCreacionUsuario y Json.toJson
public class UsuarioController2 {

    RespuestaCreacionUsuario respu = new RespuestaCreacionUsuario();

    public Result createUsuario(Http.Request request){
        JsonNode req = request.body().asJson();
        JsonNode node = null;
        Result res = null;
        String nick = null;
        Integer age = null;



        if (req != null && req.has("nick"))
            nick = req.get("nick").asText();
        if (req != null && req.has("age"))
            age = req.get("age").asInt();

        if (nick != null && !"".equals(nick)){
            if (comprobarDuplicidad(nick) == true) {
                respu.setSuccess(false);
                respu.setMesssage("POST - Error, nick duplicado");
                node = Json.toJson(respu);
                res = (Result) Results.status(409,node);

            }
        }else{
            respu.setSuccess(false);
            respu.setMesssage("POST - Error, nick vacio");
            node = Json.toJson(respu);
            res = (Result) Results.status(400,node);
        }
        if (res == null) {
            Usuario.nicks.add(nick);
            Usuario.ages.add(age);
            respu.setSuccess(true);
            respu.setMesssage("POST - Ok. Se ha insertado el nick: " + nick  + " y edad: " + age);

            node = Json.toJson(respu);
            res = Results.status(200, node);
        }



        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public Result getUsuario(Http.Request request){
        JsonNode node = null;
        Result res = null;
        if (Usuario.nicks.size() == 0) {
            respu.setSuccess(false);
            respu.setMesssage("GET - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }

        Optional<String> index = request.queryString("index");

        if (index.isPresent()){
            System.out.println(index.get());
            try {
                res = getConIndex(Integer.parseInt(index.get()));
            }catch (NumberFormatException e){
                System.err.println("Error formato no numerico");
                respu.setSuccess(false);
                respu.setMesssage("Error formato no numerico");
                node = Json.toJson(respu);
                res = Results.badRequest(node);

            }
        }else{
            respu.setSuccess(true);
            respu.setMesssage("GET Ok - " + Usuario.nicks + " " + Usuario.ages);
            node = Json.toJson(respu);
            res = Results.ok(node);
        }


        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));

    }

    public Result updateUsuario(Integer index,Http.Request request){
        JsonNode node = null;
        Result res = null;
        Integer age = null;
        String nick = null;

        if (index < 0 || Usuario.nicks.size() <= index){
            respu.setSuccess(false);
            respu.setMesssage("PUT - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(index) == null) {
            respu.setSuccess(false);
            respu.setMesssage("PUT - Sin resultados");
            node = Json.toJson(respu);
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
                    respu.setSuccess(false);
                    respu.setMesssage("PUT - Error, nick duplicado");
                    node = Json.toJson(respu);
                    res = (Result) Results.status(409, node);
                }
            }else{
                respu.setSuccess(false);
                respu.setMesssage("PUT - Error, nick vacio");
                node = Json.toJson(respu);
                res = (Result) Results.status(400, node);
            }
            if (res == null) {
                Usuario.nicks.set(index,nick);
                Usuario.ages.set(index,age);
                respu.setSuccess(true);
                respu.setMesssage("PUT - Ok. Se ha actualizado el nick de: "+ anteriorNick +" a "+ nick +" y la edad a: " + age);
                node = Json.toJson(respu);
                res = Results.status(200, node);
            }
        }

        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public Result deleteUsuario(Integer index){
        JsonNode node = null;
        Result res = null;
        if (index < 0 || Usuario.nicks.size() <= index){
            respu.setSuccess(false);
            respu.setMesssage("DELETE - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(index) == null) {
            respu.setSuccess(false);
            respu.setMesssage("DELETE - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }


        if (res == null) {
            String s = Usuario.nicks.get(index);
            if (Usuario.nicks.remove(s)) {
                respu.setSuccess(true);
                respu.setMesssage("DELETE Ok -\"" + s);
                node = Json.toJson(respu);
                res = Results.ok(node);
            }else{
                respu.setSuccess(false);
                respu.setMesssage("DELETE Error al borrar -" + s);
                node = Json.toJson(respu);
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
        JsonNode node = null;
        Result res = null;
        if (in < 0 || Usuario.nicks.size() <= in){
            respu.setSuccess(false);
            respu.setMesssage("GET - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }else if (Usuario.nicks.get(in) == null) {
            respu.setSuccess(false);
            respu.setMesssage("GET - Sin resultados");
            node = Json.toJson(respu);
            res = Results.notFound(node);
        }

        if (res == null) {
            respu.setSuccess(true);
            respu.setMesssage("GET Ok - " + Usuario.nicks.get(in) + " " +Usuario.ages.get(in));
            node = Json.toJson(respu);
            res = Results.ok(node);
        }

        return res;
    }

}

//JsonIgnoreProperties({"success","message"})
class RespuestaCreacionUsuario {

    @JsonProperty("is_success")
    boolean success;
    //@JsonIgnore
    String message;

    public boolean isSuccess(){
        return success;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public String getMesssage(){
        return message;
    }

    public void setMesssage(String message){
        this.message = message;
    }
}