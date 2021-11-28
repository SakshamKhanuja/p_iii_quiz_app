package com.project.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.project.quiz_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Performs View Binding.
    private ActivityMainBinding mBinding;

    // Title of Quiz English.
    private static final String ENGLISH = "English";

    // Title of Quiz Mathematics.
    private static final String MATHEMATICS = "Mathematics";

    private final String CHIP_SUBJECT_KEY = "selectedSubjectChip";

    private final String PRESSED_FAB_KEY = "userPressedFab";

    private final String SELECTED_QUIZ_KEY = "currentQuiz";

    private final String PROPER_ORDER_KEY = "orderedClicks";

    /**
     * Indicates in what order the user started a Quiz.
     * <p>
     * 0 - Press FAB "->" | Check Subject ----- WRONG Order
     * 1 - Check Subject | Press FAB "->" ----- CORRECT Order
     */
    private int userStartedQuizInOrder = 0;

    /**
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
        mBinding.groupSubjects.setOnCheckedChangeListener((group, checkedID) ->
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

        /*
         * Registering a callback when "Question 3 - English - c." RadioButtons are selected by
         * the user.
         */
        mBinding.groupRadioEQ3C.setOnCheckedChangeListener((group, checkedId) ->
                fillBlank(mBinding.blankEQ3C, checkedId, 2));

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

        // Clear EditText "Question 2 - C".
        mBinding.entryEQ2C.setText("");

        // Clear blank TextView "Question 3 - A".
        mBinding.blankEQ3A.setText("");

        // Uncheck the checked "Question 3 - A".
        mBinding.groupRadioEQ3A.clearCheck();

        // Clear blank TextView "Question 3 - B".
        mBinding.blankEQ3B.setText("");

        // Uncheck the checked "Question 3 - B".
        mBinding.groupRadioEQ3B.clearCheck();

        // Clear blank TextView "Question 3 - C".
        mBinding.blankEQ3C.setText("");

        // Uncheck the checked "Question 3 - C".
        mBinding.groupRadioEQ3C.clearCheck();
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
        mBinding.entryMQ3A.setText("");

        // Clear EditText in "Question 3 - B".
        mBinding.entryMQ3B.setText("");

        // Clear EditText in "Question 3 - C".
        mBinding.entryMQ3C.setText("");
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
                    mBinding.groupSubjects.check(savedInstanceState.getInt(CHIP_SUBJECT_KEY,
                            0));
                    showSection(0);
                }
            } else if (checkedChipKey != 0) {
                // Show the checked Chip and Quiz Picker Section.
                mBinding.groupSubjects.check(savedInstanceState.getInt(CHIP_SUBJECT_KEY,
                        0));
                showSection(0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Backing up the current checked Subject.
        outState.putInt(CHIP_SUBJECT_KEY, mBinding.groupSubjects.getCheckedChipId());

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
            mBinding.fabStart.setVisibility(View.GONE);

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
            mBinding.fabStart.setVisibility(View.VISIBLE);
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
        mBinding.groupSubjects.clearCheck();
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