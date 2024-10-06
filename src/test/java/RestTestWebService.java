import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Arrays;
import java.util.List;

public class RestTestWebService {

    int id1 = 1;
    int id2 = 2;
    int id3 = 3;

    String vasya = "Vasya";
    String petya = "Petya";
    String masha = "Masha";

    int mk2 = 2;
    int mk3 = 3;
    int mk4 = 4;
    int mk5 = 5;

    List<Integer> marksNull = Arrays.asList();
    List<Integer> marks1 = Arrays.asList(mk5, mk5, mk5);
    List<Integer> marks2 = Arrays.asList(mk4, mk4, mk4);
    List<Integer> marks3 = Arrays.asList(mk3, mk4, mk5, mk2);
    List<Integer> marks4 = Arrays.asList(mk5, mk5, mk5, mk5);

    public Student getStudent(int id) {
        Student st = RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .extract().as(Student.class);
        return st;
    }

    @SneakyThrows
    public Student addStudent(String name, List<Integer> marks) {
        Student st = new Student();
        st.setName(name);
        st.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then();
        return st;

    }

    @SneakyThrows
    public Student addStudent(int id, List<Integer> marks) {
        Student st = new Student();
        st.setId(id);
        st.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then();
        return st;
    }

    @SneakyThrows
    public Student addStudent(int id, String name, List<Integer> marks) {
        Student st = new Student();
        st.setId(id);
        st.setName(name);
        st.setMarks(marks);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then();
        return st;
    }

    public void deleteStudent(int id) {
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then();
    }

    @Test
    @SneakyThrows
    public void test1ExistingStudent() {
        Student st = addStudent(id1, vasya, marks1);
        int id = st.getId();
        String name = st.getName();
        Student st1 = RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(id))
                .body("name", Matchers.equalTo(name))
                .extract().as(Student.class);
        deleteStudent(id);
    }

    @Test
    @SneakyThrows
    public void test2NonExistingStudent() {
        int id = -1;
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @SneakyThrows
    public void test3AddStudentWithIDAndName() {
        Student st = addStudent(id1, vasya, marks1);
        int id = st.getId();
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then()
                .statusCode(201);
        deleteStudent(id);
    }

    @Test
    @SneakyThrows
    public void test4UpdateStudentWithIDAndName4() {
        Student st1 = addStudent(1, vasya, marks1);
        Student st2 = addStudent(1, petya, marks1);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st2))
                .when()
                .post()
                .then()
                .statusCode(201);
        Student st3 = getStudent(st2.getId());
        Assert.assertEquals(st3.getId(), st1.getId());
        Assert.assertEquals(st3.getName(), st2.getName());
        deleteStudent(st2.getId());
    }

    @Test
    @SneakyThrows
    public void test5AddStudentWithoutID() {
        Student st = new Student();
        st.setName(vasya);
        st.setMarks(marks1);
        ObjectMapper mapper = new ObjectMapper();
        String str = RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().asString();
        deleteStudent(Integer.parseInt(str));
    }

    @Test
    @SneakyThrows
    public void test6AddStudentWithoutName() {
        Student st = addStudent(1, marks1);
        ObjectMapper mapper = new ObjectMapper();
        RestAssured.given()
                .baseUri("http://localhost:8080/student")
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsString(st))
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @SneakyThrows
    public void test7DeleteExistingStudent() {
        Student st = addStudent(id1, vasya, marks1);
        int id = st.getId();
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .statusCode(200);
        deleteStudent(id);
    }

    @Test
    @SneakyThrows
    public void test8DeleteNonExistingStudent() {
        int id = -1;
        RestAssured.given()
                .baseUri("http://localhost:8080/student/" + id)
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .statusCode(404);
    }

    @Test
    @SneakyThrows
    public void test9GetTopStudent() {
        String str = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().asString();
        Assert.assertEquals(str, "");
    }

    @Test
    @SneakyThrows
    public void test10GetTopStudent() {
        Student st1 = addStudent(id1, vasya, marksNull);
        String str = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().asString();
        Assert.assertEquals(str, "");
        deleteStudent(st1.getId());
    }

    @Test
    @SneakyThrows
    public void test11GetTopStudent() {
        Student st1 = addStudent(id1, vasya, marks1);
        Student st2 = addStudent(id2, petya, marks4);
        Student st3 = addStudent(id3, masha, marks3);
        List<Student> students = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", Student.class);
        Assert.assertEquals(students.size(), 1);
        deleteStudent(st1.getId());
        deleteStudent(st2.getId());
        deleteStudent(st3.getId());
    }

    @Test
    @SneakyThrows
    public void test12ingGetTopStudents() {
        Student st1 = addStudent(id1, vasya, marks4);
        Student st2 = addStudent(id2, petya, marks4);
        Student st3 = addStudent(id3, masha, marks3);
        List<Student> students = RestAssured.given()
                .baseUri("http://localhost:8080/topStudent")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", Student.class);
        Assert.assertEquals(students.size(), 2);
        deleteStudent(st1.getId());
        deleteStudent(st2.getId());
        deleteStudent(st3.getId());
    }
}
