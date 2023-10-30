package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GameTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void 자동차이름들_구분자로_구분() {
        String separator = ",";
        String input = "vega,npc";
        String[] result = input.split(separator);

        assertThat(result).contains("npc", "vega");
        assertThat(result).containsExactly("vega", "npc");
    }

    @Test
    void 자동차수가_주어진_최소치보다_작을경우() { //null의 경우는 너무 단순하게 처리되어서 테스트에 포함x
        int minimumLength = 2;
        String input = "vega,npc1";
        String[] result = input.split(",");

        assertThat(result).hasSizeGreaterThanOrEqualTo(minimumLength);
    }

    @Test
    void 자동차이름들_중복검사() {
        String input = "vega,npc1";
        String[] result = input.split(",");
        List<String> resultList = Arrays.asList(result);
        Set<String> resultSet = new HashSet<>(resultList);

        assertThat(resultSet.size()).isEqualTo(result.length);
    }

    @Test
    void validate_라운드횟수_숫자인지() {
        String input = "7a";

        assertThatThrownBy(() -> Integer.parseInt(input))
                .isInstanceOf(NumberFormatException.class)
                .hasMessageContaining("For input string: \"7a\"");
    }

    @Test
    void validate_라운드횟수_null인지() {
        String input = "7";

        assertThat(input).isNotNull();
    }

    @Test
    void validate_라운드횟수_0보다큰지() {
        String input = "1";

        assertThat(Integer.parseInt(input)).isGreaterThan(0);
    }

    @Test
    void 라운드진행_정상진행되는지() {
        Car obj1 = new Car("npc1");
        Car obj2 = new Car("npc2");
        Game gameObj = new Game();

        gameObj.carList.add(obj1);
        gameObj.carList.add(obj2);
        gameObj.roundNumber = 5;

        try {
            gameObj.proceedEachRound();
        } catch (Exception e) {
            fail("proceedEachRound 함수의 실행이 정상적으로 처리되지 않았음");
        }
    }

    @Test
    void 우승자선별_전진횟수최대값() {
        Car obj1 = new Car("npc1");
        Car obj2 = new Car("npc2");
        Car obj3 = new Car("npc3");
        Game gameObj = new Game();

        gameObj.carList.add(obj1);
        gameObj.carList.add(obj2);
        gameObj.carList.add(obj3);
        gameObj.roundNumber = 5;

        obj1.increaseForwardCount(5);
        obj2.increaseForwardCount(3);
        obj3.increaseForwardCount(2);

        List<String> winnerList = gameObj.selectWinner();
        assertThat(winnerList).containsExactly("npc1");

        obj3.increaseForwardCount(3);

        winnerList = gameObj.selectWinner();
        assertThat(winnerList).containsExactly("npc1", "npc3");
    }

    @Test
    void 최종우승자_출력확인() {
        Car obj1 = new Car("npc1");
        Car obj2 = new Car("npc2");
        Game gameObj = new Game();

        gameObj.carList.add(obj1);
        gameObj.carList.add(obj2);
        gameObj.roundNumber = 5;

        obj1.increaseForwardCount(1);
        obj2.increaseForwardCount(1);

        List<String> winnerList = gameObj.selectWinner();
        assertThat(winnerList).containsExactly("npc1", "npc2");

        obj1.increaseForwardCount(1);
        obj2.increaseForwardCount(1);

        winnerList = gameObj.selectWinner();
        gameObj.announceWinner(winnerList);

        assertThat(outContent.toString()).isEqualTo("최종 우승자 : npc1, npc2");
    }
}
