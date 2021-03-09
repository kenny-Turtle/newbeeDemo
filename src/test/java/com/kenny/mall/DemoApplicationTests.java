package com.kenny.mall;

import com.kenny.mall.service.NewBeeMallGoodsService;
import com.kenny.mall.service.impl.NewBeeMallGoodsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class DemoApplicationTests {



    @Test
    void contextLoads() {
    }

    @Test
    public void test1(){
        List<User> list = new ArrayList<User>(){
            {
                add(new User(1,"kenny",23,"温州大学"));
                add(new User(2, "jay", 24, "宁波大学"));
                add(new User(3, "turtle", 25, "温州大学"));
                add(new User(4, "jacson", 24, "温州大学"));
                add(new User(5, "gdrgon", 25, "宁波大学"));
                add(new User(6, "kakasi", 26, "木叶忍者学院"));
                add(new User(7, "naruto", 27, "木叶忍着学院"));
                add(new User(8, "sasika", 27, "木叶忍着学院"));
                add(new User(9, "madara", 40, "木叶忍着学院"));
                add(new User(10, "yongdaimei", 40, "木叶忍者学院"));
            }
        };
        System.out.println("学校是忍者学院的学生");
        System.out.println(list);
        List<User>users = list.stream().filter(user -> ("木叶忍着学院").equals(user.getSchool())).collect(Collectors.toList());
        users.forEach(user -> System.out.println(user.getName()));
    }

}
class streamTest{
    List<User> list;

    @BeforeTestClass
    public void init(){
        list= new ArrayList<User>(){
            {
                add(new User(1,"kenny",23,"温州大学"));
                add(new User(2, "jay", 24, "宁波大学"));
                add(new User(3, "turtle", 25, "温州大学"));
                add(new User(4, "jacson", 24, "温州大学"));
                add(new User(5, "gdrgon", 25, "宁波大学"));
                add(new User(6, "kakasi", 26, "木叶忍者学院"));
                add(new User(7, "naruto", 27, "木叶忍着学院"));
                add(new User(8, "sasika", 27, "木叶忍着学院"));
                add(new User(9, "madara", 40, "木叶忍着学院"));
                add(new User(10, "yongdaimei", 40, "木叶忍者学院"));
            }
        };
    }
}
class User{
    public int id;
    public String name;
    public int age;
    public String school;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", school='" + school + '\'' +
                '}';
    }

    public User(int id, String name, int age, String school) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.school = school;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
