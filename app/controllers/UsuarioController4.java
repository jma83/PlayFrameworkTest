package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Usuario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//FormFactory (solo post y get)
public class UsuarioController4 {
    @Inject
    FormFactory formFactory;

    List<Usuario> users = new ArrayList<Usuario>();

    public Result createUsuario(Http.Request request){
        Form<Usuario> form = formFactory.form(Usuario.class).bindFromRequest(request);
        Result res = null;
        Usuario usu = form.get();
        Map<String,String> map = form.rawData();

        int tipo = -1;

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        Usuario.nicks.add(usu.getNick());
        Usuario.ages.add(usu.getAge());
        this.users.add(new Usuario(usu.getNick(),usu.getAge()));

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
