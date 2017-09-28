/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.frontend.editor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duy.pascal.frontend.DLog;
import com.duy.pascal.frontend.R;
import com.duy.pascal.frontend.autocomplete.autofix.command.AutoFixCommand;
import com.duy.pascal.frontend.autocomplete.autofix.dialog.QuickFixDialog;
import com.duy.pascal.frontend.autocomplete.completion.model.Description;
import com.duy.pascal.frontend.code.CompileManager;
import com.duy.pascal.frontend.code.sample.activities.DocumentActivity;
import com.duy.pascal.frontend.editor.view.AutoIndentEditText;
import com.duy.pascal.frontend.editor.view.EditorView;
import com.duy.pascal.frontend.file.util.FileUtils;
import com.duy.pascal.frontend.setting.PascalPreferences;
import com.duy.pascal.frontend.structure.DialogProgramStructure;
import com.duy.pascal.frontend.structure.viewholder.StructureItem;
import com.duy.pascal.frontend.themefont.activities.ThemeFontActivity;
import com.duy.pascal.frontend.view.exec_screen.console.ConsoleView;
import com.duy.pascal.interperter.ast.CodeUnitParsingException;
import com.duy.pascal.interperter.ast.codeunit.CodeUnit;
import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContextMixin;
import com.duy.pascal.interperter.builtin_libraries.io.IOLib;
import com.duy.pascal.interperter.core.PascalCompiler;
import com.duy.pascal.interperter.declaration.program.PascalProgramDeclaration;
import com.duy.pascal.interperter.exceptions.parsing.ParsingException;
import com.duy.pascal.interperter.exceptions.parsing.define.MainProgramNotFoundException;
import com.duy.pascal.interperter.source.FileScriptSource;
import com.duy.pascal.interperter.source.ScriptSource;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class EditorActivity extends BaseEditorActivity implements
        DrawerLayout.DrawerListener {

    public static final int ACTION_FILE_SELECT_CODE = 1012;
    public static final int ACTION_PICK_MEDIA_URL = 1013;
    public static final int ACTION_CREATE_SHORTCUT = 1014;

    private CompileManager mCompileManager;
    private MenuEditor mMenuEditor;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompileManager = new CompileManager(this);
        mDrawerLayout.addDrawerListener(this);

        mMenuEditor = new MenuEditor(this, this);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return mMenuEditor.onOptionsItemSelected(item);
            }
        });
        findViewById(R.id.img_tab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTab(v);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mMenuEditor.onOptionsItemSelected(item);
    }


    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    private void insertTab(View v) {
        onKeyClick(v, AutoIndentEditText.TAB_CHARACTER);
    }

    @Override
    public void onKeyClick(View view, String text) {
        EditorFragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.insert(text);
        }
    }

    @Override
    public void onKeyLongClick(String text) {
        EditorFragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.insert(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mMenuEditor.onCreateOptionsMenu(menu);
    }

    /**
     * create dialog find and replace
     */
    @Override
    public void findAndReplace() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setView(R.layout.dialog_find_and_replace);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final CheckBox ckbRegex = alertDialog.findViewById(R.id.ckb_regex);
        final CheckBox ckbMatch = alertDialog.findViewById(R.id.ckb_match_key);
        final EditText editFind = alertDialog.findViewById(R.id.txt_find);
        final EditText editReplace = alertDialog.findViewById(R.id.edit_replace);
        editFind.setText(getPreferences().getString(PascalPreferences.LAST_FIND));
        alertDialog.findViewById(R.id.btn_replace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
                if (editorFragment != null) {
                    editorFragment.doFindAndReplace(
                            editFind.getText().toString(),
                            editReplace.getText().toString(),
                            ckbRegex.isChecked(),
                            ckbMatch.isChecked());
                }
                getPreferences().put(PascalPreferences.LAST_FIND, editFind.getText().toString());
                alertDialog.dismiss();
            }
        });
        alertDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    public void runProgram() {
        if (doCompile()) mCompileManager.execute(getCurrentFilePath());
    }

    @Override
    public boolean isAutoSave() {
        return mMenuEditor.getChecked(R.id.action_auto_save);
    }

    /**
     * replace dialog find
     */
    public void showDialogFind() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setView(R.layout.dialog_find);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final CheckBox ckbRegex = alertDialog.findViewById(R.id.ckb_regex);
        final CheckBox ckbMatch = alertDialog.findViewById(R.id.ckb_match_key);
        final CheckBox ckbWordOnly = alertDialog.findViewById(R.id.ckb_word_only);
        final EditText editFind = alertDialog.findViewById(R.id.txt_find);
        editFind.setText(getPreferences().getString(PascalPreferences.LAST_FIND));
        alertDialog.findViewById(R.id.btn_replace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
                if (editorFragment != null) {
                    editorFragment.doFind(editFind.getText().toString(),
                            ckbRegex.isChecked(),
                            ckbWordOnly.isChecked(),
                            ckbMatch.isChecked());
                }
                getPreferences().put(PascalPreferences.LAST_FIND, editFind.getText().toString());
                alertDialog.dismiss();
            }
        });
        alertDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    public void saveFile() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.saveFile();
        }
    }

    @Override
    public void showDocumentActivity() {
        Intent intent = new Intent(this, DocumentActivity.class);
        startActivity(intent);
    }

    private void showLineError(final ParsingException e) {
        if (e != null) {
            if (e.getLineInfo() != null) {
                EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
                if (editorFragment != null) {
                    editorFragment.setLineError(e.getLineInfo());
                }
            }
        }
    }

    public String getCode() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            return editorFragment.getCode();
        }
        return "";
    }

    /**
     * compile code, if is error, show dialog error
     * invalidate keyword
     */
    @Override
    public boolean doCompile() {
        saveFile();
        String filePath = getCurrentFilePath();
        if (filePath.isEmpty()) return false;
        try {
            CodeUnit codeUnit;
            if (getCode().trim().toLowerCase().startsWith("unit ")) {

                ArrayList<ScriptSource> searchPath = new ArrayList<>();
                searchPath.add(new FileScriptSource(new File(filePath).getParent()));
                codeUnit = PascalCompiler.loadLibrary(new File(filePath).getName(),
                        new FileReader(filePath),
                        searchPath,
                        new ProgramHandler(filePath));
            } else {

                ArrayList<ScriptSource> searchPath = new ArrayList<>();
                searchPath.add(new FileScriptSource(new File(filePath).getParent()));

                codeUnit = PascalCompiler.loadPascal(new File(filePath).getName(),
                        new FileReader(filePath), searchPath, new ProgramHandler(filePath));
                if (codeUnit != null) {
                    if (((PascalProgramDeclaration) codeUnit).main == null) {
                        showErrorDialog(new MainProgramNotFoundException());
                        return false;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            showErrorDialog(e);
            return false;
        } catch (CodeUnitParsingException e) {
            showErrorDialog(e.getParseException());
            showLineError(e.getParseException());
            return false;
        } catch (Exception e) {
            showErrorDialog(e);
            return false;
        }
        Toast.makeText(this, R.string.compile_ok, Toast.LENGTH_SHORT).show();
        return true;
    }

    private void showErrorDialog(Exception e) {
        QuickFixDialog quickFixDialog = QuickFixDialog.newInstance(e);
        quickFixDialog.show(getSupportFragmentManager(), QuickFixDialog.TAG);
        DLog.e(e);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPreferences().isShowListSymbol()) {
            mKeyList.setListener(this);
            mContainerSymbol.setVisibility(View.VISIBLE);
        } else {
            mContainerSymbol.setVisibility(View.GONE);
        }
    }


    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @NonNull String s) {
        if (s.equals(getString(R.string.key_show_suggest_popup))
                || s.equals(getString(R.string.key_show_line_number))
                || s.equals(getString(R.string.key_pref_word_wrap))) {
            EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
            if (editorFragment != null) {
                editorFragment.refreshCodeEditor();
            }
        } else if (s.equals(getString(R.string.key_show_symbol))) {
            mContainerSymbol.setVisibility(getPreferences().isShowListSymbol()
                    ? View.VISIBLE : View.GONE);
        } else if (s.equals(getString(R.string.key_show_suggest_popup))) {
            EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
            if (editorFragment != null) {
                EditorView editor = editorFragment.getEditor();
                editor.setSuggestData(new ArrayList<Description>());
            }
        }
        //toggle ime/no suggest mode
        else if (s.equalsIgnoreCase(getString(R.string.key_ime_keyboard))) {
            EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
            if (editorFragment != null) {
                EditorView editor = editorFragment.getEditor();
                editorFragment.refreshCodeEditor();
            }
        } else {
            super.onSharedPreferenceChanged(sharedPreferences, s);
        }
    }

    @Override
    public boolean onSelectFile(@NonNull File file) {
        if (FileUtils.canEdit(file)) {
            //save current file
            addNewPageEditor(file);
            //close drawer
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

    @Override
    public boolean onFileLongClick(@NonNull File file) {
        showFileInfo(file);
        return false;
    }


    /**
     * show dialog with file info
     * filePath, path, size, extension ...
     *
     * @param file - file to show info
     */
    private void showFileInfo(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(file.getName());
        builder.setView(R.layout.dialog_view_file);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView txtInfo = dialog.findViewById(R.id.txt_info);
        txtInfo.setText(file.getPath());
        EditorView editorView = dialog.findViewById(R.id.editor_view);
        editorView.setTextHighlighted(mFileManager.fileToString(file));
    }

    @Override
    public void goToLine() {
        final AppCompatEditText edittext = new AppCompatEditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        edittext.setMaxEms(5);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.goto_line)
                .setView(edittext)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String line = edittext.getText().toString();
                        if (!line.isEmpty()) {
                            EditorFragment editorFragment
                                    = mPagerAdapter.getCurrentFragment();
                            if (editorFragment != null) {
                                editorFragment.goToLine(Integer.parseInt(line));
                            }
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    @Override
    public void formatCode() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.formatCode();
        }
    }

    @Override
    public void reportBug() {
        String link = "https://github.com/tranleduy2000/pascalnide/issues";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    @Override
    public void openTool() {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void undo() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.undo();
        }
    }

    @Override
    public void redo() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.redo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    String path;
                    try {
                        path = mFileManager.getPathFromUri(this, uri);
                        mFileManager.setWorkingFilePath(path);
                        addNewPageEditor(new File(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ACTION_PICK_MEDIA_URL:
                if (resultCode == RESULT_OK) {
                    String path = data.getData().toString();
                    EditorFragment currentFragment = mPagerAdapter.getCurrentFragment();
                    if (currentFragment != null && path != null) {
                        currentFragment.insert(path);
                    }
                }
                break;
            case ACTION_CREATE_SHORTCUT:
                data.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                getApplicationContext().sendBroadcast(data);
                break;
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        closeKeyBoard();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (mFabMenu.isOpened()) {
            mFabMenu.close(false);
        }
    }

    @Override
    public void paste() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.paste();
        }
    }

    @Override
    public void copyAll() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            editorFragment.copyAll();
        }
    }

    @Override
    public void selectThemeFont() {
        startActivity(new Intent(this, ThemeFontActivity.class));
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)
                || mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (getPreferences().getBoolean(getString(R.string.key_back_undo))) {
            undo();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit)
                .setMessage(R.string.exit_mgs)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditorActivity.super.onBackPressed();
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    public void openDrawer(int gravity) {
        mDrawerLayout.openDrawer(gravity);
    }

    private String getCurrentFilePath() {
        EditorFragment editorFragment = mPagerAdapter.getCurrentFragment();
        if (editorFragment != null) {
            return editorFragment.getFilePath();
        }
        return "";
    }

    public void showProgramStructure() {
        try {
            String filePath = getCurrentFilePath();
            PascalProgramDeclaration pascalProgram = PascalCompiler
                    .loadPascal(filePath, new FileReader(filePath),
                            new ArrayList<ScriptSource>(), null);

            if (pascalProgram.main == null) {
                showErrorDialog(new MainProgramNotFoundException());
            }
            ExpressionContextMixin program = pascalProgram.getProgram();

            StructureItem node = null;
//            node = getNode(program, pascalProgram.getProgramName(), StructureType.TYPE_PROGRAM, 0);

            DialogProgramStructure dialog = DialogProgramStructure.newInstance(node);
            dialog.show(getSupportFragmentManager(), DialogProgramStructure.TAG);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startDebug() {
        if (doCompile()) mCompileManager.debug(getCurrentFilePath());
    }

    public void insertColor() {
        ColorPickerDialogBuilder.with(this).
                setPositiveButton(getString(R.string.select), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        EditorFragment currentFragment = mPagerAdapter.getCurrentFragment();
                        if (currentFragment != null) {
                            currentFragment.insert(String.valueOf(lastSelectedColor));
                            Toast.makeText(EditorActivity.this, getString(R.string.inserted_color) + lastSelectedColor,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).build().show();
    }


    public void executeCommand(AutoFixCommand command) {
        EditorFragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.exciteCommand(command);
        }
    }


    private class ProgramHandler implements com.duy.pascal.frontend.runnable.ProgramHandler {

        private String programPath;

        ProgramHandler(String programPath) {

            this.programPath = programPath;
        }

        @Override
        public String getCurrentDirectory() {
            return new File(programPath).getParent();
        }

        @Override
        public Context getApplicationContext() {
            return EditorActivity.this.getApplicationContext();
        }

        @Override
        public Activity getActivity() {
            return EditorActivity.this;
        }


        @Override
        public void startInput(IOLib lock) {

        }

        @Override
        public void print(CharSequence charSequence) {

        }

        @Nullable
        @Override
        public ConsoleView getConsoleView() {
            return null;
        }

        @Override
        public void println(CharSequence charSequence) {

        }

        @Override
        public char getKeyBuffer() {
            return 0;
        }

        @Override
        public boolean keyPressed() {
            return false;
        }

        @Override
        public void clearConsole() {

        }
    }
}