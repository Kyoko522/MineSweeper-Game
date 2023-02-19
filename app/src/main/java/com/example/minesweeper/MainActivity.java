package com.example.minesweeper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    int row = 8;
    int col = 8;
    int thegameisover = 0;
    ImageView[] pics = new ImageView[row * col];
    //the field hold the mine and how many namours it has
    int[][] field = new int[row][col];
    //to see if it has been open or not
    int[][] show = new int[row][col];
    //if they have places a flag or not
    int[][] flags = new int[row][col];
    //just a random array that help the computer may a condition where they remove a flag it doesn't reset the whole board
    int[][] aflag = new int[row][col];
    //for the falg button so it can either me clicked to turn on the flag or turn it off to reveal the square under
    boolean flag = false;
    //thsi variable are used so that the dialog box only shows once so it not annoying to teh user
    int box = 1;
    int box2 = 1;
    int[][] pain = new int[row][col];
    //this should be the same as how many bomb there are on the field
    //so for the easy mode there is only 10 bombs so they should be able to place 10 or less bombs on the feaid
    Timer timer;
    ProgressBar pb;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout g = (GridLayout) findViewById(R.id.grid);
        int m = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                pics[m] = new ImageView(this);
                setpicStart(pics[m], m);
                pics[m].setId(m);
                pics[m].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClick(v.getId());
                    }
                });

                g.addView(pics[m]);
                m++;
            }
        }
        //run the addmine method
        addMines(10);
        //run the neighbouring method
        neighbours();
    }


    //make the board my defualt blank
    public void setpicStart(ImageView i, int pos) {
        int x = pos / col;
        int y = pos % col;
        i.setImageResource(R.drawable.blank);
    }

    //clear everything so make the whole board back
    //this means that you reset all the array to defualt values
    public void clearAll() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                field[i][j] = 0;
                show[i][j] = 0;
                flags[i][j] = 0;
                flag = false;
                //since this is a new game you will have to rest the box so that if there is another user they get the instructions
                box = 1;

            }
        }
    }

    //add mine to the board
    public void addMines(int amt) {
        //find a random position on the board
        for (int i = 0; i < amt; i++) {
            int x = (int) (Math.random() * row);
            int y = (int) (Math.random() * col);
            //and if the position already has a mine then pick another position
            while (field[x][y] != 0) {
                x = (int) (Math.random() * row);
                y = (int) (Math.random() * col);
            }
            //and finally place a mine in that location
            field[x][y] = 10;
        }
    }

    //when the user step on the egg the board will reveal itself showing all the mine and neighbours
    public void showAll() {
        //a loop to check it position on the board
        int m = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                //based on what number is saved in the feild it willl be change the image
                if (field[i][j] == 0) pics[m].setImageResource(R.drawable.emp);
                else if (field[i][j] == 1) pics[m].setImageResource(R.drawable.num1);
                else if (field[i][j] == 2) pics[m].setImageResource(R.drawable.num2);
                else if (field[i][j] == 3) pics[m].setImageResource(R.drawable.num3);
                else if (field[i][j] == 4) pics[m].setImageResource(R.drawable.num4);
                else if (field[i][j] == 5) pics[m].setImageResource(R.drawable.num5);
                else if (field[i][j] == 6) pics[m].setImageResource(R.drawable.num6);
                else if (field[i][j] == 7) pics[m].setImageResource(R.drawable.num7);
                else if (field[i][j] == 8) pics[m].setImageResource(R.drawable.num8);
                else pics[m].setImageResource(R.drawable.mine);
                m++;


            }
        }
    }

    //the reset method will stop that task on the screen and reset all the procress
    public void reset(View view) {
        //this is where the box comes in play if the box is 1 then it will show the instruction
        if (box == 1) {
            //this dialog box is like a warning tell them if they reset they will not be able to go back to his screen and will loss all procress in the game
            new AlertDialog.Builder(this)
                    //The title on the Dialog
                    .setTitle("Are you Sure")
                    //The message that will appear
                    .setMessage("If you decide to reset you will lose all progress you have made. You will have to start from the beginning of these level +\n" + "Click reset again to confirm")
                    //What to do if the button is pressed
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //do something if they click the button
//otherwise, it just dismisses the dialog
                        }
                    }).show();
            //it changes the box from 1 to 2 making to that the user will never see the message again
            box = 2;
            //if the box is 2 and they click on reset again then it will reset the game
        } else if (box == 2) {
            thegameisover = 0;
            clearAll();
            finish();
            startActivity(getIntent());
        }
    }

    //neighbouring is the number of bombs that square  is touching
    public void neighbours() {
        //run a loop going through every single spot on teh board
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (field[i][j] != 10) {
                    int count = 0;
                    //and basic on waht is around the box you click ti will count the number of bombs(or eggs in this game)
                    if (i - 1 >= 0 && j - 1 >= 0 && field[i - 1][j - 1] == 10) count++;
                    if (i - 1 >= 0 && field[i - 1][j] == 10) count++;
                    if (i - 1 >= 0 && j + 1 < col && field[i - 1][j + 1] == 10) count++;
                    if (i + 1 < row && j - 1 >= 0 && field[i + 1][j - 1] == 10) count++;
                    if (i + 1 < row && field[i + 1][j] == 10) count++;
                    if (i + 1 < row && j + 1 < col && field[i + 1][j + 1] == 10) count++;
                    if (j - 1 >= 0 && field[i][j - 1] == 10) count++;
                    if (j + 1 < col && field[i][j + 1] == 10) count++;
                    field[i][j] = count;
                }
            }
        }
    }

    //opent the board if there is no number near the spot that is clicked
    public void open(int i, int j) {
        if (field[i][j] != 0) return;
        if (i - 1 >= 0 && j - 1 >= 0 && show[i - 1][j - 1] == 0) {
            show[i - 1][j - 1] = 1;
            if (field[i - 1][j - 1] == 0) open(i - 1, j);
        }
        if (i - 1 >= 0 && show[i - 1][j] == 0) {
            show[i - 1][j] = 1;
            if (field[i - 1][j - 1] == 0) open(i - 1, j);
        }
        if (i - 1 >= 0 && j + 1 < col && show[i - 1][j + 1] == 0) {
            show[i - 1][j + 1] = 1;
            if (field[i - 1][j + 1] == 0) open(i - 1, j + 1);
        }
        if (i + 1 < row && show[i + 1][j] == 0) {
            show[i + 1][j] = 1;
            if (field[i + 1][j] == 0) open(i + 1, j);
        }
        if (i + 1 < row && j + 1 < col && show[i + 1][j + 1] == 0) {
            show[i + 1][j + 1] = 1;
            if (field[i + 1][j + 1] == 0) open(i + 1, j + 1);
        }
        if (j - 1 >= 0 && show[i][j - 1] == 0) {
            show[i][j - 1] = 1;
            if (field[i][j - 1] == 0) open(i, j + 1);
        }
        if (j + 1 < col && show[i][j + 1] == 0) {
            show[i][j + 1] = 1;
            if (field[i][j + 1] == 0) open(i, j + 1);
        }
        if (i + 1 < row && j - 1 >= 0 && show[i + 1][j - 1] == 0) {
            show[i + 1][j - 1] = 1;
            if (field[i + 1][j - 1] == 0) open(i + 1, j - 1);
        }
    }

    //switched the flag to true or false
    public void flag(View view) {
        ImageView flagpic = (ImageView) findViewById(R.id.changeflag);
        if (flag) {
            flag = false;
            flagpic.setImageResource(R.drawable.blank);
        } else {
            flag = true;
            flagpic.setImageResource(R.drawable.flagsel);
            if (box2 == 1) {
                //create a dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("OK", null);
                //build the image to add to the dialog box
                ImageView i = new ImageView(this);
                i.setImageResource(R.drawable.flagsel);
                builder.setView(i);
                //pick the other settings
                builder.setTitle("Flag");
                builder.setMessage("When the flag is blue that means you can place a flag to spot on the grid where you think there is a dragon egg");
                //show the dialog box
                builder.show();
                //this make it so the hint doesn't keep showing up and resets
                box2 = 2;
            }
        }
    }

    //updates the screen same as otehr screen
    public void redraw() {
        if (thegameisover == 1) {
            //create a dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", null);
            //pick the other settings
            builder.setTitle("The Game is Over");
            builder.setMessage("The game is over you must reset to continue");
            //show the dialog box
            builder.show();
        } else {


            int m = 0;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (show[i][j] == 1 && flags[i][j] == 1 && flag == true) {
                        pics[m].setImageResource(R.drawable.flag);
                        aflag[i][j] = 1;

                    } else if (show[i][j] == 1 && flags[i][j] == 0 && flag == true && aflag[i][j] == 1) {
                        pics[m].setImageResource(R.drawable.blank);
                        show[i][j] = 0;
                        aflag[i][j] = 0;

                    } else if (show[i][j] == 1 && flags[i][j] == 0 && flag == false) {
                        if (field[i][j] == 0) pics[m].setImageResource(R.drawable.emp);
                        else if (field[i][j] == 1) pics[m].setImageResource(R.drawable.num1);
                        else if (field[i][j] == 2) pics[m].setImageResource(R.drawable.num2);
                        else if (field[i][j] == 3) pics[m].setImageResource(R.drawable.num3);
                        else if (field[i][j] == 4) pics[m].setImageResource(R.drawable.num4);
                        else if (field[i][j] == 5) pics[m].setImageResource(R.drawable.num5);
                        else if (field[i][j] == 6) pics[m].setImageResource(R.drawable.num6);
                        else if (field[i][j] == 7) pics[m].setImageResource(R.drawable.num7);
                        else if (field[i][j] == 8) pics[m].setImageResource(R.drawable.num8);
                        else if (field[i][j] == 9) pics[m].setImageResource(R.drawable.blank);
                        else pics[m].setImageResource(R.drawable.mine);
                    }
                    m++;
                }
            }
        }
    }

    //same as the other screen
    public void gridButtonClick(int pos) {
        int x = pos / col;
        int y = pos % col;
        // please explain why u named it pain to future self
        if (flag && pain[x][y] == 0) {
            flags[x][y] = 1;
            pain[x][y] = 1;

        } else if (flag && pain[x][y] == 1) {
            flags[x][y] = 0;
            pain[x][y] = 0;

        }
        show[x][y] = 1;
        if (!flag) open(x, y);
        redraw();
        win(pos);
    }

    //when hint is click goes toi the hint screen
    public void Instruction (View view) {
//        Intent intent = new Intent(MineSweeper.this, mainminesweeperinstruction.class);
//        startActivity(intent);
    }

    //commented on anotehr screen
    public void win(int pos) {
        int correctplacedflag = 0;
        int maxflag = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

                if (field[i][j] == 10 && flags[i][j] == 1) correctplacedflag++;

                if (flags[i][j] == 1) maxflag++;

                if (maxflag > 10) {
                    Toast.makeText(getApplicationContext(), "There are too many flags on the board please remove some", Toast.LENGTH_SHORT).show();
                }
            }
            if (correctplacedflag == 10 && maxflag <= 10) {
                Toast.makeText(getApplicationContext(), "Congratulation you have won", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        //The title on the Dialog
                        .setTitle("Congratulation")
                        //The message that will appear
                        .setMessage("You have successfully found all the mine")
                        //What to do if the button is pressed
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do something if they click the button
//otherwise, it just dismisses the dialog
//                                Intent intent = new Intent(MineSweeper.this, task2description.class);
//                                startActivity(intent);
                            }
                        }).show();
            }
            int x = pos / col;
            int y = pos % col;

            if (field[x][y] == 10 && flags[x][y] == 0 && show[x][y] == 1 && aflag[x][y] == 0) {
                showAll();
                Toast.makeText(getApplicationContext(), "Game over you step on a mine", Toast.LENGTH_SHORT).show();
                box = 2;
                thegameisover = 1;
//              Intent intent = new Intent(MineSweeper.this, taskoneloss.class);
//              startActivity(intent);
            }
        }
    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // Just put this in. You need it.
    }


}