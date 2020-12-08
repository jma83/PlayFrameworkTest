package models;

import java.util.ArrayList;

public class Usuario {
    static public ArrayList<String> nicks = new ArrayList<String>();
    static public ArrayList<Integer> ages = new ArrayList<Integer>();

    String nick;
    Integer age;

    public Usuario(String nick, Integer age){
        this.nick = nick;
        this.age = age;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String name) {
        this.nick = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


}
