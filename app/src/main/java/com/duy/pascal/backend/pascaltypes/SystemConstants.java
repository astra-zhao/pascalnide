package com.duy.pascal.backend.pascaltypes;


import com.duy.pascal.backend.lib.graph.style.LineStyle;
import com.duy.pascal.backend.lib.graph.style.LineWidth;
import com.duy.pascal.backend.lib.graph.text_model.TextDirection;
import com.duy.pascal.backend.lib.graph.text_model.TextFont;
import com.duy.pascal.backend.lib.graph.text_model.TextJustify;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.js.interpreter.ast.ConstantDefinition;
import com.js.interpreter.ast.expressioncontext.ExpressionContextMixin;

/**
 * Define system constant when init parser
 * Created by Duy on 01-Mar-17.
 */
public class SystemConstants {

    public static void addSystemConstant(ExpressionContextMixin context) {
        defineColorCode(context);
        defineGraphConstant(context);
    }

    private static void defineGraphConstant(ExpressionContextMixin context) {
        ConstantDefinition colorConst;
        colorConst = new ConstantDefinition("grok".toLowerCase(), 1, new LineInfo(-1, "grok = 1;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("NormWidth".toLowerCase(), LineWidth.NormWidth,
                new LineInfo(-1, "const NormWidth = 1;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("ThickWidth".toLowerCase(), LineWidth.ThickWidth,
                new LineInfo(-1, "const ThickWidth = 3;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("SolidLn".toLowerCase(), LineStyle.SolidLn,
                new LineInfo(-1, "const SolidLn = 0;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("DottedLn".toLowerCase(), LineStyle.DottedLn,
                new LineInfo(-1, "const DottedLn = 1;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("CenterLn".toLowerCase(), LineStyle.CenterLn,
                new LineInfo(-1, "const CenterLn = 2;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("DashedLn".toLowerCase(), LineStyle.DashedLn,
                new LineInfo(-1, "const DashedLn = 3;".toLowerCase()));
        context.declareConst(colorConst);

        //Font number: Normal font
        colorConst = new ConstantDefinition("DefaultFont".toLowerCase(), TextFont.TriplexFont,
                new LineInfo(-1, "const DefaultFont = 0;".toLowerCase()));
        context.declareConst(colorConst);

        //        Font number: Triplex font
        colorConst = new ConstantDefinition("TriplexFont".toLowerCase(), TextFont.TriplexFont,
                new LineInfo(-1, "const TriplexFont = 1;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("SmallFont".toLowerCase(), TextFont.SmallFont,
                new LineInfo(-1, "const SmallFont = 2;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("SansSerifFont".toLowerCase(), TextFont.SansSerifFont,
                new LineInfo(-1, "const SansSerifFont = 3;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("GothicFont".toLowerCase(), TextFont.GothicFont,
                new LineInfo(-1, "const GothicFont = 4;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("ScriptFont".toLowerCase(), TextFont.ScriptFont,
                new LineInfo(-1, "const ScriptFont = 5;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("SimpleFont".toLowerCase(), TextFont.SimpleFont,
                new LineInfo(-1, "const SimpleFont = 6;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("TSCRFont".toLowerCase(), TextFont.TSCRFont,
                new LineInfo(-1, "const TSCRFont = 7;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("LCOMFont".toLowerCase(), TextFont.LCOMFont,
                new LineInfo(-1, "const LCOMFont = 8;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("EuroFont".toLowerCase(), TextFont.EuroFont,
                new LineInfo(-1, "const EuroFont  = 9;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("BoldFont".toLowerCase(), TextFont.BoldFont,
                new LineInfo(-1, "const EuroFont  = 10;".toLowerCase()));
        context.declareConst(colorConst);

        //text direction
        colorConst = new ConstantDefinition("HorizDir".toLowerCase(), TextDirection.HORIZONTAL_DIR,
                new LineInfo(-1, "const HorizDir = 0;".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("VertDir".toLowerCase(), TextDirection.VERTICAL_DIR,
                new LineInfo(-1, "const VertDir   = 1;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("HorizDir".toLowerCase(), TextDirection.HORIZONTAL_DIR,
                new LineInfo(-1, "const HorizDir = 0;".toLowerCase()));
        context.declareConst(colorConst);

        ///////////////////////////
        colorConst = new ConstantDefinition("LeftText".toLowerCase(), TextJustify.HORIZONTAL_STYLE.LeftText,
                new LineInfo(-1, "const LeftText   = 0;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("CenterText".toLowerCase(), TextJustify.HORIZONTAL_STYLE.CenterText,
                new LineInfo(-1, "const CenterText   = 1;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("RightText".toLowerCase(), TextJustify.HORIZONTAL_STYLE.RightText,
                new LineInfo(-1, "const RightText   = 2;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("BottomText".toLowerCase(), TextJustify.VERTICAL_STYLE.BottomText,
                new LineInfo(-1, "const BottomText   = 0;".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("TopText".toLowerCase(), TextJustify.VERTICAL_STYLE.TopText,
                new LineInfo(-1, "const TopText   = 2;".toLowerCase()));
        context.declareConst(colorConst);


    }

    /**
     * value  color
     * 0    Black
     * 1    Blue
     * 2    Green
     * 3    Cyan
     * 4    Red
     * 5    Magenta
     * 6    Brown
     * 7    White
     * 8    Grey
     * 9    Light Blue
     * 10    Light Green
     * 11    Light Cyan
     * 12    Light Red
     * 13    Light Magenta
     * 14    Yellow
     * 15    High-intensity white
     */
    private static void defineColorCode(ExpressionContextMixin context) {
        ConstantDefinition colorConst;
        colorConst = new ConstantDefinition("black".toLowerCase(), 0, new LineInfo(-1, "black = 0".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Blue".toLowerCase(), 1, new LineInfo(-1, "Blue = 1".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Green".toLowerCase(), 2, new LineInfo(-1, "Green = 2".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Cyan".toLowerCase(), 3, new LineInfo(-1, "Cyan = 3".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Red".toLowerCase(), 4, new LineInfo(-1, "Red = 4".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Magenta".toLowerCase(), 5, new LineInfo(-1, "Magenta = 5".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Brown".toLowerCase(), 6, new LineInfo(-1, "Brown = 6".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightGray".toLowerCase(), 7, new LineInfo(-1, "LightGray  = 7".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("DarkGray".toLowerCase(), 8, new LineInfo(-1, "DarkGray = 8".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightBlue".toLowerCase(), 9, new LineInfo(-1, "LightBlue = 9".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightGreen".toLowerCase(), 10, new LineInfo(-1, "LightGreen = 10".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightCyan".toLowerCase(), 11, new LineInfo(-1, "LightCyan = 11".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightRed".toLowerCase(), 12, new LineInfo(-1, "LightRed = 12".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("LightMagenta".toLowerCase(), 13, new LineInfo(-1, "LightMagenta = 13".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("Yellow".toLowerCase(), 14, new LineInfo(-1, " Yellow = 14".toLowerCase()));
        context.declareConst(colorConst);
        colorConst = new ConstantDefinition("White".toLowerCase(), 15, new LineInfo(-1, "White = 15".toLowerCase()));
        context.declareConst(colorConst);

        colorConst = new ConstantDefinition("pi".toLowerCase(), Math.PI, new LineInfo(-1, " pi = 3.14159265358979323846".toLowerCase()));
        context.declareConst(colorConst);
    }

    /**
     * add some missing type
     *
     * @param context
     */
    public static void addSystemType(ExpressionContextMixin context) {
        context.declareTypedef("text", BasicType.Integer);
    }
}
