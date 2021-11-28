package com.project.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.project.quiz_app.databinding.ActivityMainBinding;

/**
 * This application lets a user pick among two subject quizzes -
 * <p>
 * 1. English
 * 2. Mathematics
 * <p>
 * After submitting the quiz, a Toast shows the user's final score.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Naming Convention:
     * <p>
     * --Views
     * 1. Chip - chip
     * 2. ChipGroup - group_chip
     * 3. TextView having background {@link R.drawable#shape_blank} - blank
     * 4. EditText having background {@link R.drawable#shape_hint} - entry
     * 5. CheckBox - checkBox
     * 6. RadioButton - radio
     * 7. RadioGroup - group_radio
     * <p>
     * --View ID -> [ViewType_QuizType_QuestionNumber_QuestionPart_[SubPart/Hint]]
     * Example: mBinding.groupChipEQ1A / findViewById(R.id.group_chip_e_q1_a)
     * The above ID represents "ChipGroup" View under "a." of "Question 1" in "English" quiz.
     * Link -> {@link R.id#group_chip_e_q1_a}
     * <p>
     * --String Naming Convention -> Go to "strings.xml".
     */

    // Performs View Binding.
    private ActivityMainBinding mBinding;

    // Title of Quiz English.
    private static final String ENGLISH = "English";

    // Title of Quiz Mathematics.
    private static final String MATHEMATICS = "Mathematics";

    // English Quiz Max Score.
    private static final int QUIZ_ENGLISH_SCORE = 22;

    // Mathematics Quiz Max Score.
    private static final int QUIZ_MATHEMATICS_SCORE = 12;

    // Used in restoring Views after orientation change.
    private final String CHIP_SUBJECT_KEY = "selectedSubjectChip";

    // Used in restoring Views after orientation change.
    private final String PRESSED_FAB_KEY = "userPressedFab";

    // Used in restoring Views after orientation change.
    private final String SELECTED_QUIZ_KEY = "currentQuiz";

    // Used in restoring Views after orientation change.
    private final String PROPER_ORDER_KEY = "orderedClicks";

    /*
     * Indicates in which order the user has pressed buttons in the "Quiz Picker" Section.
     * <p>
     * 0 - Press FAB "->" | Check Subject
     * 1 - Check Subject | Press FAB "->"
     *
     * This check avoids an invalid start of a quiz.
     *
     * Assuming the user has clicked the FAB "->" first, followed by checking a Subject
     * (Chip - English / Mathematics). At this moment if user rotates their phone, initially
     * the selected subject Quiz will automatically start. This check prevents that.
     */
    private int userStartedQuizInOrder = 0;

    /*
     * Indicates the state of FAB "->".
     * <p>
     * 0 - User has not pressed the FAB.
     * 1 - User has pressed the FAB.
     */
    private int userPressedFAB = 0;

    // Shows messages to the user.
    private Toast mToast;

    /*
     * Represents the ongoing quiz.
     *
     * 0 - User hasn't selected any subjects.
     * 1 - User selected English.
     * 2 - User selected Mathematics.
     */
    private int currentSubject = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Replacing use-case of "findViewById" with View Binding.
        mBinding = ActivityMainBinding.inflate(
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(mBinding.getRoot());

        // Registering a callback when subject Chips are selected / de-selected by the user.
        mBinding.groupChipSubjects.setOnCheckedChangeListener((group, checkedID) ->
                selectSubject(checkedID)
        );

        // Registering a callback when the FAB "->" is clicked by the user.
        mBinding.fabStart.setOnClickListener(view -> startQuiz());

        // Registering a callback when the "Reset" TextView is clicked.
        mBinding.textViewReset.setOnClickListener(view -> reset());

        /*
         * Registering a callback when "Question 1 - English - a." Chips are selected /
         * de-selected by the user.
         */
        mBinding.groupChipEQ1A.setOnCheckedChangeListener((group, checkedId) ->
                fillBlank(mBinding.blankEQ1A, checkedId, 1));

        /*
         * Registering a callback when "Question 1 - English - b." Chips are selected /
         * de-selected by the user.
         */
        mBinding.groupChipEQ1B.setOnCheckedChangeListener((group, checkedId) ->
                fillBlank(mBinding.blankEQ1B, checkedId, 1));

        /*
         * Registering a callback when "Question 3 - English - a." RadioButtons are selected by
         * the user.
         */
        mBinding.groupRadioEQ3A.setOnCheckedChangeListener((group, checkedId) ->
                fillBlank(mBinding.blankEQ3A, checkedId, 2));

        /*
         * Registering a callback when "Question 3 - English - b." RadioButtons are selected by
         * the user.
         */
        mBinding.groupRadioEQ3B.setOnCheckedChangeListener((group, checkedId) ->
                fillBlank(mBinding.blankEQ3B, checkedId, 2));

        // Registering a callback when the "Submit" TextView is clicked.
        mBinding.textViewSubmit.setOnClickListener(view -> submitQuiz(currentSubject));

        // Restore the current progress - If device is rotated.
        if (savedInstanceState != null) {
            restoreCurrentProgress(savedInstanceState);
        }
    }

    /**
     * Fills up the blank based on user choice.
     *
     * @param blank     is the TexView having shape {@link R.drawable#shape_blank}.
     * @param checkedID is the ID of the selected View.
     * @param viewType  is type of the selected View.
     *                  1 - Chip
     *                  2 - RadioButton
     */
    private void fillBlank(TextView blank, int checkedID, int viewType) {
        String text = "";
        if (checkedID != View.NO_ID) {
            if (viewType == 1) {
                Chip selectedChip = findViewById(checkedID);
                text = selectedChip.getText().toString();
            } else if (viewType == 2) {
                RadioButton selectedRadioButton = findViewById(checkedID);
                text = selectedRadioButton.getText().toString();
            }
        }
        blank.setText(text);
    }

    /**
     * Resets English Quiz.
     */
    private void resetEnglishQuiz() {
        // Clear blank TextView "Question 1 - A".
        mBinding.blankEQ1A.setText("");

        // Uncheck the checked "Question 1 - A".
        mBinding.groupChipEQ1A.clearCheck();

        // Clear blank TextView "Question 1 - B".
        mBinding.blankEQ1B.setText("");

        // Uncheck the checked "Question 1 - B".
        mBinding.groupChipEQ1B.clearCheck();

        // Clear EditText "Question 2 - A".
        mBinding.entryEQ2A.setText("");

        // Clear EditText "Question 2 - B".
        mBinding.entryEQ2B.setText("");

        // Clear blank TextView "Question 3 - A".
        mBinding.blankEQ3A.setText("");

        // Clear blank TextView "Question 3 - B".
        mBinding.blankEQ3B.setText("");

        // Uncheck the checked "Question 3 - A".
        mBinding.groupRadioEQ3A.clearCheck();

        // Uncheck the checked "Question 3 - B".
        mBinding.groupRadioEQ3B.clearCheck();
    }

    /**
     * Resets Mathematics Quiz.
     */
    private void resetMathematicsQuiz() {
        // Uncheck CheckBox's in "Question 1".
        mBinding.checkboxMQ1I.setChecked(false);
        mBinding.checkboxMQ1II.setChecked(false);
        mBinding.checkboxMQ1III.setChecked(false);
        mBinding.checkboxMQ1IV.setChecked(false);

        // Clear EditText in "Question 2".
        mBinding.entryMQ2.setText("");

        // Clear EditText in "Question 3 - A".
        mBinding.entryMQ3.setText("");
    }

    /**
     * Restores the current progress.
     */
    private void restoreCurrentProgress(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            // Restore all backed up values.
            int checkedChipKey = savedInstanceState.getInt(CHIP_SUBJECT_KEY, 0);
            userPressedFAB = savedInstanceState.getInt(PRESSED_FAB_KEY, 0);
            currentSubject = savedInstanceState.getInt(SELECTED_QUIZ_KEY, 0);
            userStartedQuizInOrder = savedInstanceState.getInt(PROPER_ORDER_KEY, 0);

            // Check if user has already selected a Subject and pressed the FAB "->".
            if (checkedChipKey != 0 && userPressedFAB == 1) {
                // Check if the order to open up the Subject Quiz was correct.
                if (userStartedQuizInOrder == 1) {
                    // Show the ongoing selected Quiz.
                    showSection(currentSubject);
                } else {
                    // Show the checked Chip and Quiz Picker Section.
                    mBinding.groupChipSubjects.check(savedInstanceState.getInt(CHIP_SUBJECT_KEY,
                            0));
                    showSection(0);
                }
            } else if (checkedChipKey != 0) {
                // Show the checked Chip and Quiz Picker Section.
                mBinding.groupChipSubjects.check(savedInstanceState.getInt(CHIP_SUBJECT_KEY,
                        0));
                showSection(0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Backing up the current checked Subject.
        outState.putInt(CHIP_SUBJECT_KEY, mBinding.groupChipSubjects.getCheckedChipId());

        // Backing up whether user has pressed the FAB "->".
        outState.putInt(PRESSED_FAB_KEY, userPressedFAB);

        // Backing up user selected quiz.
        outState.putInt(SELECTED_QUIZ_KEY, currentSubject);

        // Backing up whether user started quiz in correct order.
        outState.putInt(PROPER_ORDER_KEY, userStartedQuizInOrder);
    }

    /**
     * Starts the quiz based on user selection in the Quiz Picker section.
     */
    private void startQuiz() {
        // Indicate user has pressed the FAB "->".
        userPressedFAB = 1;

        // Show section.
        showSection(currentSubject);

        // Send Toasts to user.
        if (currentSubject != 0) {
            // User opened up the Quiz in correct order.
            userStartedQuizInOrder = 1;

            showToast(R.string.toastSelected);
        } else {
            // User tried opening in wrong order.
            userStartedQuizInOrder = 0;

            showToast(R.string.toastNotSelected);
        }
    }

    /**
     * Shows a section of the app based on the current checked Subject.
     *
     * @param currentSubject is the current checked Subject.
     */
    private void showSection(int currentSubject) {
        if (currentSubject != 0) {
            // Hides the Quiz picker.
            mBinding.sectionSubjectPicker.setVisibility(View.GONE);
            mBinding.textviewTitle.setVisibility(View.GONE);
            mBinding.fabStart.setVisibility(View.GONE);
            mBinding.image123.setVisibility(View.GONE);
            mBinding.imageAbc.setVisibility(View.GONE);

            // Shows the Quiz Section.
            mBinding.sectionQuiz.setVisibility(View.VISIBLE);

            switch (currentSubject) {
                case 1:
                    // Shows the "English" Quiz.
                    mBinding.quizEnglish.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    // Shows the "Mathematics" Quiz.
                    mBinding.quizMathematics.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            // Shows the Quiz Picker.
            mBinding.sectionSubjectPicker.setVisibility(View.VISIBLE);
            mBinding.textviewTitle.setVisibility(View.VISIBLE);
            mBinding.fabStart.setVisibility(View.VISIBLE);
            mBinding.image123.setVisibility(View.VISIBLE);
            mBinding.imageAbc.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Resets Everything.
     */
    private void reset() {
        // Hide the current Quiz.
        switch (currentSubject) {
            case 1:
                resetEnglishQuiz();
                // Hides the "English" Quiz.
                mBinding.quizEnglish.setVisibility(View.GONE);
                break;

            case 2:
                resetMathematicsQuiz();
                // Hides the "Mathematics" Quiz.
                mBinding.quizMathematics.setVisibility(View.GONE);
                break;
        }

        // Hide the Quiz Section.
        mBinding.sectionQuiz.setVisibility(View.GONE);

        // Scrolls the ScrollView to top.
        mBinding.sectionQuiz.fullScroll(ScrollView.FOCUS_UP);

        // Reset the currentSubject.
        currentSubject = 0;

        // Make FAB press check FALSE.
        userPressedFAB = 0;

        // Defaults user button pressed order.
        userStartedQuizInOrder = 0;

        // Show the Quiz Picker
        showSection(0);

        // Uncheck the checked Subject.
        mBinding.groupChipSubjects.clearCheck();
    }

    /**
     * Sets the {@link MainActivity#currentSubject} value based on the checked Chip.
     *
     * @param checkedID is the checked Chip ID.
     */
    private void selectSubject(int checkedID) {
        if (checkedID != View.NO_ID) {
            // Referencing to the checked Chip.
            Chip checkedChip = findViewById(checkedID);

            switch (checkedChip.getText().toString()) {
                case ENGLISH:
                    currentSubject = 1;
                    break;

                case MATHEMATICS:
                    currentSubject = 2;
                    break;
            }
        } else {
            // No subject is selected.
            currentSubject = 0;
        }
    }

    /**
     * Formats the user response in EditText having background {@link R.drawable#shape_hint}.
     *
     * @param editText contains the user response.
     * @return formatted (trimmed and lowercase) user response.
     */
    private String formatUserResponse(EditText editText) {
        return editText.getText().toString().toLowerCase().trim();
    }

    /**
     * Calculates the total score of user in English Quiz.
     *
     * @return total score.
     */
    private int calculateEnglishQuizResult() {
        int score = 0;

        // Checks Question 1 - A (5 marks).
        if (mBinding.blankEQ1A.getText().toString().equals(getString(R.string.EQ1AH1))) {
            score += 5;
        }

        // Checks Question 1 - B (5 marks).
        if (mBinding.blankEQ1B.getText().toString().equals(getString(R.string.EQ1BH1))) {
            score += 5;
        }

        // Checks Question 2 - A (3 marks).
        if (formatUserResponse(mBinding.entryEQ2A).equals(getString(R.string.ansEQ2A))) {
            score += 3;
        }

        // Checks Question 2 - B (3 marks).
        if (formatUserResponse(mBinding.entryEQ2B).equals(getString(R.string.ansEQ2B))) {
            score += 3;
        }

        // Checks Question 3 - A (3 marks).
        if (mBinding.blankEQ3A.getText().toString().equals(getString(R.string.EQ3AH3))) {
            score += 3;
        }

        // Checks Question 3 - B (3 marks).
        if (mBinding.blankEQ3B.getText().toString().equals(getString(R.string.EQ3BH1))) {
            score += 3;
        }

        return score;
    }

    /**
     * Calculates the total score of user in Mathematics Quiz.
     *
     * @return total score.
     */
    private int calculateMathematicsQuizResult() {
        int score = 0;

        // Checks Question 1 (5 marks).
        if (mBinding.checkboxMQ1I.isChecked() && mBinding.checkboxMQ1III.isChecked() &&
                mBinding.checkboxMQ1IV.isChecked() && !mBinding.checkboxMQ1II.isChecked()) {
            score += 5;
        }

        // Checks Question 2 (5 marks).
        if (formatUserResponse(mBinding.entryMQ2).equals(getString(R.string.ansMQ2))) {
            score += 5;
        }

        // Checks Question 3 (2 marks).
        if (formatUserResponse(mBinding.entryMQ3).equals(getString(R.string.ansMQ3))) {
            score += 2;
        }

        return score;
    }

    /**
     * Calculates how much the user scored in the ongoing quiz. The total score is then showed
     * to the user via Toast.
     *
     * @param currentSubject is the subject of the ongoing Quiz.
     *                       1 - English
     *                       2 - Mathematics
     */
    private void submitQuiz(int currentSubject) {
        if (currentSubject == 1) {
            showToast(getString(R.string.quizScore, calculateEnglishQuizResult(),
                    QUIZ_ENGLISH_SCORE));
        } else if (currentSubject == 2) {
            showToast(getString(R.string.quizScore, calculateMathematicsQuizResult(),
                    QUIZ_MATHEMATICS_SCORE));
        }
    }

    /**
     * Removes any Toast which is currently showing and replaces it with a new message.
     *
     * @param message is the new message.
     */
    private void showToast(String message) {
        // Remove toast if currently showing.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * Removes any Toast which is currently showing and replaces it with a new message.
     *
     * @param messageID is the String resource ID.
     */
    private void showToast(int messageID) {
        // Remove toast if currently showing.
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, messageID, Toast.LENGTH_SHORT);
        mToast.show();
    }
}