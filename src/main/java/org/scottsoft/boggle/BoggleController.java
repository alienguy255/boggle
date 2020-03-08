package org.scottsoft.boggle;

import java.util.List;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/boggle")
public class BoggleController {

    private final WordFinder wordFinder;

    @RequestMapping(value = "/solve", method = RequestMethod.POST)
    public ResponseEntity<List<String>> solve(@RequestBody char[][] board) {
        return new ResponseEntity<>(wordFinder.findWords(board), HttpStatus.OK);
    }

}
