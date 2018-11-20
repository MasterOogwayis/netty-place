import lombok.Getter;
import lombok.Setter;

/**
 * @author Shaowei Zhang on 2018/11/15 21:44
 **/
public class StaticTest {

    public static void main(String[] args) {
        Dto dto = new Dto();
        dto.setName("zsw");
        foo(dto);
        System.out.println(dto.getName());//打印结果是"Hello"
    }




    static void foo(Dto dto) {
        dto.setName("123");
    }

    @Getter
    @Setter
    static class Dto {
        String name;
    }


}
