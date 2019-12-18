package extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testReg {
    public static void main(String[] args) {
        // 被测试内容
        String str = "/**\n" +
                "     * A wrapper class that exposes only the ScheduledExecutorService\n" +
                "     * methods of a ScheduledExecutorService implementation.\n" +
                "     */\n" +
                "    class DelegatedScheduledExecutorService\n" +
                "            extends DelegatedExecutorService\n" +
                "            implements ScheduledExecutorService {";
//        String str = "/* Subtask " +
//                "constructor" +
//                "*/IntCumulateTask(IntCumulateTask parent, IntBinaryOperator function, int[] array, int origin, int fence, int threshold, int lo, int hi) {sdfadsfsdfasdfwe{";

        // 正则表达式
        String reg = "(private|static|public|abstract|final|class)[\\sa-zA-Z0-9,.<>]*?(class|interface){0,1}[\\sa-zA-Z0-9,.<>]*?\\{{1}";

        //修改上方正则表达式和测试内容点击运行就可以在线进行测试， 也可以修改下方代码


        Matcher m1 = Pattern.compile(reg).matcher(str);
        String lines="";
        int count=0;
        if (m1.find()) {
            System.out.println(m1.group());
            lines+=m1.group(0) +"\n";
            count++;
        }
        if(count>0) {
            System.out.println("找到"+count+"个相匹配结果：");
            System.out.println(lines);
        } else {
            System.out.println("未找到相匹配结果");
        }
    }
}
