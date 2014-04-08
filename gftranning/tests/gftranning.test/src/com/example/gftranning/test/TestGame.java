package com.example.gftranning.test;

import com.example.gftranning.Game;

import android.test.AndroidTestCase;

public class TestGame extends AndroidTestCase {

    public void testGame() {
        Game game = new Game(8, 0, 0, 1);
        game.setNextResults(1);
        game.setNextResults(1);
        game.setNextResults(1);
        assertEquals(1, game.getNeedCheckResultCount());
        game.checkAndRememberUserInput(true);
        
        game.setNextResults(1);
        assertEquals(2, game.getNeedCheckResultCount());
        assertTrue(game.checkAndRememberUserInput(true));
        
        game.setNextResults(2);
        assertEquals(3, game.getNeedCheckResultCount());
        assertTrue(game.checkAndRememberUserInput(true));
    }

}
