package controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import models.Usuario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Con templates (solo post y get)
public class UsuarioController3 {

    Result res = null;
    List<Usuario> users = new ArrayList<Usuario>();

    public Result createUsuario(Http.Request request){
        JsonNode req = request.body().asJson();
        Document req2 = request.body().asXml();

        System.out.println(req);
        System.out.println(req2);

        int tipo = -1;

        if (req!=null)
            res = createWithJSON(req);
        if (req2!=null)
            res = createWithXML(req2);

        if (request.accepts("application/xml")){
            tipo = 0;
        }else if (request.accepts("application/json")) {
            tipo = 1;
        }
        if (res == null) {
            if (tipo==0) {
                Content content = views.xml.usuarios.render(users);
                res = Results.ok(content);
            }else if (tipo == 1){
                res = Results.ok(Json.toJson(users));
            }
        }
        users.clear();
        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));
    }

    public Result createWithJSON(JsonNode req){
        String nick = null;
        Integer age = null;
        Result res = null;
        for (int i = 0;i <req.size();i++){
            JsonNode a = req.get(i);
            System.out.println(a.get("nick"));

            if (a.has("nick"))
                nick = a.get("nick").asText();

            if (a.has("age"))
                age = a.get("age").asInt();

            if (nick != null && !"".equals(nick)){
                if (comprobarDuplicidad(nick) == true) {
                    res = (Result) Results.status(409,"Error duplicidad");
                    break;
                }
            }else{

                res = (Result) Results.status(400,"POST - Error, nick vacio");
                break;
            }
            if (res == null) {
                Usuario.nicks.add(nick);
                Usuario.ages.add(age);
                users.add(new Usuario(nick,age));
            }

        }
        return res;
    }

    public Result createWithXML(Document req){
        String nick = null;
        Integer age = null;
        Result res = null;
        NodeList users = req.getElementsByTagName("user");

        for (int i = 0;i <users.getLength();i++){
            Element a = (Element) users.item(i);

            if (a.getElementsByTagName("nick").item(0) != null)
                nick = getTextNode((Element) a.getElementsByTagName("nick").item(0));

            if (a.getElementsByTagName("age").item(0) != null)
                age = Integer.parseInt(getTextNode((Element) a.getElementsByTagName("age").item(0)));

            if (nick != null && !"".equals(nick)){
                if (comprobarDuplicidad(nick) == true) {
                    res = (Result) Results.status(409,"Error duplicidad");
                    break;
                }
            }else{
                res = (Result) Results.status(400,"POST - Error, nick vacio");
                break;
            }
            if (res == null) {
                Usuario.nicks.add(nick);
                Usuario.ages.add(age);
                this.users.add(new Usuario(nick,age));
            }

        }

        return res;
    }

    public Result getUsuario(Http.Request request){
        Result res = null;
        int tipo = -1;

        if (Usuario.nicks.size() == 0) {
            res = Results.notFound("Sin resultados!");
        }else {

            if (request.accepts("application/xml")){
                tipo = 0;
            }else if (request.accepts("application/json")) {
                tipo = 1;
            }

            Optional<String> index = request.queryString("index");

            if (index.isPresent()) {
                System.out.println(index.get());
                try {
                    res = getConIndex(Integer.parseInt(index.get()), tipo);
                } catch (NumberFormatException e) {
                    System.err.println("Error formato no numerico");
                    res = Results.badRequest("Error formato no numerico");

                }
            } else {
                List<Usuario> users = new ArrayList<Usuario>();
                for (int i = 0; i < Usuario.nicks.size(); i++) {
                    users.add(new Usuario(Usuario.nicks.get(i), Usuario.ages.get(i)));
                }
                if (tipo == 0) {
                    Content content = views.xml.usuarios.render(users);
                    res = Results.ok(content);
                }else if (tipo == 1){
                    res = Results.ok(Json.toJson(users));
                }
            }
        }


        return res.withHeader("X-User-Count",String.valueOf(Usuario.nicks.size()));

    }

    public String getTextNode(Element e) {
        return e.getChildNodes().item(0).getNodeValue();
    }


    public boolean comprobarDuplicidad (String nick){
        for (int i = 0; i < Usuario.nicks.size(); i++) {
            if (nick.equals(Usuario.nicks.get(i))) {
                return true;
            }
        }
        return false;
    }

    public Result getConIndex(int in, int tipo){
        Result res = null;
        ObjectNode node = Json.newObject();
        if (in < 0 || Usuario.nicks.size() <= in){
            res = Results.notFound("GET - Sin resultados");
        }else if (Usuario.nicks.get(in) == null) {
            res = Results.notFound("GET - Sin resultados");
        }

        if (res == null) {
            Usuario u = new Usuario(Usuario.nicks.get(in),Usuario.ages.get(in));

            if (tipo == 0) {
                Content content = views.xml.usuario.render(u);
                res = Results.ok(content);
            }else if (tipo == 1){
                res = Results.ok(Json.toJson(u));
            }
        }

        return res;
    }

}

