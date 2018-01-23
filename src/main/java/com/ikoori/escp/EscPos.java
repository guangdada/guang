package com.ikoori.escp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ikoori.escp.params.BarCode;
import com.ikoori.escp.params.Constant;
import com.ikoori.escp.params.Goods;
import com.ikoori.escp.params.PosParam;
import com.ikoori.escp.params.PosTpl;
import com.ikoori.escp.params.QrCode;
import com.ikoori.escp.params.Text;


/**
 * 控制打印机工具类
 *
 *
 *
 * @author SubLuLu
 */
public class EscPos {

    private static String encoding = null;

    // 通过socket流进行读写
    private OutputStream socketOut = null;
    private OutputStreamWriter writer = null;

    // 以ip作为key，EscPos实例作为value的Map
    private static Map<String, EscPos> posMap = new HashMap<String, EscPos>();
    private static EscPos escPos = null;
    
    private Socket socket = null;

    /**
     * 根据ip、端口、字符编码构造工具类实例
     *
     * @param ip          打印机ip
     * @param port        打印机端口，默认9100
     * @param encoding    打印机支持的编码格式(主要针对中文)
     * @throws IOException
     */
    public EscPos(String ip, int port, String encoding) throws IOException {
        socket = new Socket(ip, port);
        socketOut = socket.getOutputStream();
//        socket.isClosed();
        EscPos.encoding = encoding;
        writer = new OutputStreamWriter(socketOut, encoding);
    }

    public synchronized static EscPos getInstance(String ip, Integer port, String encoding) throws IOException {
        escPos = posMap.get(ip);
        if (escPos == null) {
            escPos = new EscPos(ip, port, encoding);
        }
        return escPos;
    }

    public synchronized static EscPos getInstance(String ip, Integer port) throws IOException {
        return getInstance(ip, port, Constant.DEFAULT_ENCODING);
    }

    public static synchronized EscPos getInstance(String ip) throws IOException {
        return getInstance(ip, Constant.DEFAULT_PORT, Constant.DEFAULT_ENCODING);
    }

    /**
     * 根据模板内容和参数打印小票
     *
     * @param template 模板内容
     * @param param    参数
     * @throws IOException
     */
    public void print(String template, String param) throws IOException {
        PosParam posParam = JSON.parseObject(param, PosParam.class);

        Map<String, Object> keyMap = posParam.getKeys();
        List<Map<String, Object>> goodsParam = posParam.getGoods();

        // replace placeholder in template
        Pattern pattern = Pattern.compile(Constant.REPLACE_PATTERN);

        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            if(keyMap.containsKey(key)){
            	matcher.appendReplacement(sb, keyMap.get(key)+"");
            }else{
            	System.out.println("keyMap has not found : " + key);
            }
        }

        matcher.appendTail(sb);

        template = sb.toString();

        PosTpl posTpl = JSON.parseObject(template, PosTpl.class);

        // print header
        for (JSONObject jsonObject : posTpl.getHeader()) {
            print(jsonObject);
        }

        // print goods
        // print title
        for (Goods goods : posTpl.getGoods()) {
            printTitle(goods);
        }
        escPos.line(1);

        // print detail
        for (Map<String, Object> goods : goodsParam) {
            printGoods(goods, posTpl.getGoods());
        }

        // 打印警告信息
        for (JSONObject jsonObject : posTpl.getWarn()) {
            print(jsonObject);
        }

        // 打印pos下单结果
        for (JSONObject jsonObject : posTpl.getMsg()) {
            print(jsonObject);
        }
        escPos.line(1);

        escPos.feedAndCut();
        
        escPos.close();
        
    }
    
    private void close(){
    	try {
			socketOut.close();
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * 换行
     *
     * @param lineNum 换行数，0为不换行
     * @return
     * @throws IOException
     */
    private EscPos line(int lineNum) throws IOException {
        for (int i=0; i<lineNum; i++) {
            writer.write("\n");
            writer.flush();
        }
        return this;
    }

    /**
     * 下划线
     *
     * @param flag false为不添加下划线
     * @return
     * @throws IOException
     */
    private EscPos underline(boolean flag) throws IOException {
        if (flag) {
            writer.write(0x1B);
            writer.write(45);
            writer.write(2);
        }
        return this;
    }

    /**
     * 取消下划线
     *
     * @param flag true为取消下划线
     * @return
     * @throws IOException
     */
    private EscPos underlineOff(boolean flag) throws IOException {
        if (flag) {
            writer.write(0x1B);
            writer.write(45);
            writer.write(0);
        }
        return this;
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    private EscPos bold(boolean flag) throws IOException {
        if (flag) {
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
        }
        return this;
    }

    /**
     * 取消粗体
     *
     * @param flag true为取消粗体模式
     * @return
     * @throws IOException
     */
    private EscPos boldOff(boolean flag) throws IOException {
        if (flag) {
            writer.write(0x1B);
            writer.write(69);
            writer.write(0);
        }
        return this;
    }

    /**
     * 排版
     *
     * @param position 0：居左(默认) 1：居中 2：居右
     * @return
     * @throws IOException
     */
    private EscPos align(int position) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        writer.write(position);
        return this;
    }

    /**
     * 初始化打印机
     *
     * @return
     * @throws IOException
     */
    private EscPos init() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        return this;
    }

    /**
     * 二维码排版对齐方式
     *
     * @param position   0：居左(默认) 1：居中 2：居右
     * @param moduleSize 二维码version大小
     * @return
     * @throws IOException
     */
    private EscPos alignQr(int position, int moduleSize) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        if (position == 1) {
            writer.write(1);
            centerQr(moduleSize);
        } else if (position == 2){
            writer.write(2);
            rightQr(moduleSize);
        } else {
            writer.write(0);
        }
        return this;
    }

    /**
     * 居中牌排列
     *
     * @param moduleSize  二维码version大小
     * @throws IOException
     */
    private void centerQr(int moduleSize) throws IOException {
        switch (moduleSize) {
            case 1 :{
                printSpace(16);
                break;
            }
            case 2 : {
                printSpace(18);
                break;
            }
            case 3 :{
                printSpace(20);
                break;
            }
            case 4 : {
                printSpace(22);
                break;
            }
            case 5 : {
                printSpace(24);
                break;
            }
            case 6 : {
                printSpace(26);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 二维码居右排列
     *
     * @param moduleSize  二维码version大小
     * @throws IOException
     */
    private void rightQr(int moduleSize) throws IOException {
        switch (moduleSize) {
            case 1 :
                printSpace(14);
                break;
            case 2 :
                printSpace(17);
                break;
            case 3 :
                printSpace(20);
                break;
            case 4 :
                printSpace(23);
                break;
            case 5 :
                printSpace(26);
                break;
            case 6 :
                printSpace(28);
                break;
            default:
                break;
        }
    }

    /**
     * 打印空白
     *
     * @param length  需要打印空白的长度
     * @throws IOException
     */
    private void printSpace(int length) throws IOException {
        for (int i=0; i<length; i++) {
        	writer.write("  ");  
        }
        writer.flush();
    }

    /**
     * 字体大小
     *
     * @param size 1-8 选择字号
     * @return
     * @throws IOException
     */
    private EscPos size(int size) throws IOException {
        int fontSize;
        switch (size) {
            case 1:
                fontSize = 0;
                break;
            case 2:
                fontSize = 17;
                break;
            case 3:
                fontSize =34;
                break;
            case 4:
                fontSize = 51;
                break;
            case 5:
                fontSize = 68;
                break;
            case 6:
                fontSize = 85;
                break;
            case 7:
                fontSize = 102;
                break;
            case 8:
                fontSize = 119;
                break;
            default:
                fontSize = 0;
        }
        writer.write(0x1D);
        writer.write(33);
        writer.write(fontSize);
        return this;
    }

    /**
     * 重置字体大小
     *
     * @return
     * @throws IOException
     */
    private EscPos sizeReset() throws IOException {
        writer.write(0x1B);
        writer.write(33);
        writer.write(0);
        return this;
    }

    /**
     * 进纸并全部切割
     *
     * @return
     * @throws IOException
     */
    private EscPos feedAndCut() throws IOException {
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        writer.write(0);
        writer.flush();
        return this;
    }

    /**
     * 打印条形码
     *
     * @param value
     * @return
     * @throws IOException
     */
    private EscPos barCode(String value) throws IOException {
        writer.write(0x1D);
        writer.write(107);
        writer.write(67);
        writer.write(value.length());
        writer.write(value);
        writer.flush();
        return this;
    }
    
	/**
	 * 打印条码
	 * 
	 * @param code
	 *            String to be encoded in the barcode. Different barcodes have
	 *            different requirements on the length of data that can be
	 *            encoded.
	 * @param type
	 *            Specify the type of barcode 65 = UPC-A. 66 = UPC-E. 67 =
	 *            JAN13(EAN). 68 = JAN8(EAN). 69 = CODE39. 70 = ITF. 71 =
	 *            CODABAR. 72 = CODE93. 73 = CODE128.
	 * 
	 * @param h
	 *            height of the barcode in points (1 <= n <= 255)
	 * @param w
	 *            width of module (2 <= n <=6). Barcode will not print if this
	 *            value is too large. 
	 * @param font 
	 * 			  Set font of HRI characters 0 = font A 1 = font B
	 * @param pos
	 *            set position of HRI characters 0 = not printed. 1 = Above
	 *            barcode. 2 = Below barcode. 3 = Both abo ve and below barcode.
	 */
	private EscPos barcode(String code, int type, int h, int w,
			int pos) throws IOException {

		// need to test for errors in length of code
		// also control for input type=0-6
		// GS H = HRI position
		writer.write(0x1D);
		writer.write("H");
		writer.write(pos); // 0=no print, 1=above, 2=below, 3=above & below

		// GS f = set barcode characters
		writer.write(0x1D);
		writer.write("f");
//		writer.write(font);
		writer.write(1);

		// GS h = sets barcode height
		writer.write(0x1D);
		writer.write("h");
		writer.write(h);

		// GS w = sets barcode width
		writer.write(0x1D);
		writer.write("w");
		writer.write(w);// module = 1-6

		// GS k
		writer.write(0x1D); // GS
		writer.write("k"); // k
		writer.write(type);// m = barcode type 0-6
		writer.write(code.length()); // length of encoded string
		writer.write(code);// d1-dk
		writer.write(0);// print barcode

		writer.flush();
		return this;
	}

    /**
     * 打印二维码
     *
     * @param qrData
     * @return
     * @throws IOException
     */
    private EscPos qrCode(int position, String qrData) throws IOException {
        int moduleSize = 4;
        int length = qrData.getBytes(encoding).length;
        int l = (int) (Math.ceil(1.5*length) * 8);
        if (l<200) {
            moduleSize = 1;
        } else if (l<429) {
            moduleSize = 2;
        } else if (l<641) {
            moduleSize = 3;
        } else if (l<885) {
            moduleSize = 4;
        } else if (l<1161) {
            moduleSize = 5;
        } else if (l<1469) {
            moduleSize = 6;
        }
//        alignQr(position, moduleSize);

        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

        return this;
    }

    /** 
     * 打印二维码 
     * 
     * @param qrData 二维码的内容 
     * @throws IOException 
     */  
    protected void qrCode(String qrData) throws IOException {  
        int moduleSize = 8;  
        int length = qrData.getBytes(encoding).length;  
  
        //打印二维码矩阵  
        writer.write(0x1D);// init  
        writer.write("(k");// adjust height of barcode  
        writer.write(length + 3); // pl  
        writer.write(0); // ph  
        writer.write(49); // cn  
        writer.write(80); // fn  
        writer.write(48); //  
        writer.write(qrData);  
  
        writer.write(0x1D);  
        writer.write("(k");  
        writer.write(3);  
        writer.write(0);  
        writer.write(49);  
        writer.write(69);  
        writer.write(48);  
  
        writer.write(0x1D);  
        writer.write("(k");  
        writer.write(3);  
        writer.write(0);  
        writer.write(49);  
        writer.write(67);  
        writer.write(moduleSize);  
  
        writer.write(0x1D);  
        writer.write("(k");  
        writer.write(3); // pl  
        writer.write(0); // ph  
        writer.write(49); // cn  
        writer.write(81); // fn  
        writer.write(48); // m  
  
        writer.flush();  
  
    }  

    /**
     * 打印字符串
     *
     * @param str 所需打印字符串
     * @return
     * @throws IOException
     */
    private EscPos printStr(String str) throws IOException {
        writer.write(str);
        writer.flush();
        return this;
    }

    /**
     * 打印任何对象
     *
     * @param jsonObject  需要输出对象
     * @throws IOException
     */
    private static void print(JSONObject jsonObject) throws IOException {
        int type = jsonObject.getInteger("type");

        switch (type) {
            case 0:
                Text text = JSON.toJavaObject(jsonObject, Text.class);
                printText(text);
                break;
            case 1:
                BarCode barCode = JSON.toJavaObject(jsonObject, BarCode.class);
                printBarCode(barCode);
                break;
            case 2:
                QrCode qrCode = JSON.toJavaObject(jsonObject, QrCode.class);
                printQrCode(qrCode);
                break;
//            case 3:
//                Image image = JSON.toJavaObject(jsonObject, Image.class);
//                printImage(image);
//                break;
        }
    }

    /**
     * 打印纯文本
     *
     * @param text  文本内容
     * @throws IOException
     */
    private static void printText(Text text) throws IOException {
        escPos.align(text.getFormat())
                .bold(text.isBold())
                .underline(text.isUnderline())
                .size(text.getSize())
                .printStr(text.getText())
                .boldOff(text.isBold())
                .underlineOff(text.isUnderline())
                .sizeReset()
                .line(text.getLine());
    }

    /**
     * 打印条形码
     *
     * @param barCode   条形码内容
     * @throws IOException
     */
    private static void printBarCode(BarCode barCode) throws IOException {
        escPos.align(barCode.getFormat())
                .barcode(barCode.getText(), barCode.getPosType(), barCode.getHeight(), barCode.getWidth(), barCode.getFontShow())
                .line(barCode.getLine());
    }

    /**
     * 打印二维码
     *
     * @param qrCode   二维码内容
     * @throws IOException
     */
    private static void printQrCode(QrCode qrCode) throws IOException {
        escPos.qrCode(qrCode.getFormat(), qrCode.getText())
                .line(qrCode.getLine());
    }

    /**
     * 打印商品小票的列名
     *
     * @param goods
     * @throws IOException
     */
    private static void printTitle(Goods goods) throws IOException {
        escPos.bold(false)
                .underline(false)
                .size(1)
                .printStr(goods.getName())
                .boldOff(false)
                .underlineOff(false)
                .sizeReset()
                .line(0)
                .printSpace(goods.getWidth());
    }

    /**
     * 循环打印商品信息
     *
     * @param goods
     * @param goodsList
     * @throws IOException
     */
    private static void printGoods(Map<String, Object> goods, List<Goods> goodsList) throws IOException {
        for (Goods ele : goodsList) {
            escPos.bold(false)
                    .underline(false)
                    .size(1)
                    .printStr(addBlank(goods.get(ele.getVariable())+"", ele.getWidth() * 2))
                    .boldOff(false)
                    .underlineOff(false)
                    .line(0);
        }
        escPos.line(1);
    }

	private static String addBlank(String str, int length){
		
		int len = str.length();
		if(len>length){
			return str.substring(0, length);
		}
		for (int i = 0; i < length-len; i++) {
			str += "  ";
		}
		
		return str;
	}
	
	/**
	 * 打印二维码
	 * 
	 * @param str
	 *            String to be encoded in QR.
	 * @param errCorrection
	 *            The degree of error correction. (48 <= n <= 51) 48 = level L /
	 *            7% recovery capacity. 49 = level M / 15% recovery capacity. 50
	 *            = level Q / 25% recovery capacity. 51 = level H / 30% recovery
	 *            capacity.
	 * 
	 * @param moduleSize
	 *            The size of the QR module (pixel) in dots. The QR code will
	 *            not print if it is too big. Try setting this low and
	 *            experiment in making it larger.
	 */
	public void printQR(String str, int errCorrect, int moduleSize)
			throws IOException {
		// save data function 80
		writer.write(0x1D);// init
		writer.write("(k");// adjust height of barcode
		writer.write(str.length() + 3); // pl
		writer.write(0); // ph
		writer.write(49); // cn
		writer.write(80); // fn
		writer.write(48); //
		writer.write(str);

		// error correction function 69
		writer.write(0x1D);
		writer.write("(k");
		writer.write(3); // pl
		writer.write(0); // ph
		writer.write(49); // cn
		writer.write(69); // fn
		writer.write(errCorrect); // 48<= n <= 51

		// size function 67
		writer.write(0x1D);
		writer.write("(k");
		writer.write(3);
		writer.write(0);
		writer.write(49);
		writer.write(67);
		writer.write(moduleSize);// 1<= n <= 16

		// print function 81
		writer.write(0x1D);
		writer.write("(k");
		writer.write(3); // pl
		writer.write(0); // ph
		writer.write(49); // cn
		writer.write(81); // fn
		writer.write(48); // m

		writer.flush();
		escPos.line(1);

        escPos.feedAndCut();
        
        escPos.close();
	}

}
