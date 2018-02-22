package co.solinx.kafka.monitor.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/3/28.
 */
public class DateUtils {
    /**
     * 数据库存储的时间格式串，如yyyymmdd 或yyyymmddHHmmss
     */
    public static final int DB_STORE_DATE = 1;

    /**
     * 用连字符-分隔的时间时间格式串，如yyyy-mm-dd 或yyyy-mm-dd HH:mm:ss
     */
    public static final int HYPHEN_DISPLAY_DATE = 2;

    /**
     * 用连字符.分隔的时间时间格式串，如yyyy.mm.dd 或yyyy.mm.dd HH:mm:ss
     */
    public static final int DOT_DISPLAY_DATE = 3;

    /**
     * 用中文字符分隔的时间格式串，如yyyy年mm月dd 或yyyy年mm月dd HH:mm:ss
     */
    public static final int CN_DISPLAY_DATE = 4;

    public static String toDisplayStr(String dateStr, int formatType)
    {
        if (formatType < DB_STORE_DATE || formatType > CN_DISPLAY_DATE)
        {
            throw new IllegalArgumentException("时间格式化类型不是合法的值。");
        }
        if (dateStr == null || dateStr.length() < 6 || dateStr.length() > 14
                || formatType == DB_STORE_DATE)
        {
            return dateStr;
        } else
        {
            char[] charArr = null;
            switch (formatType)
            {
                case HYPHEN_DISPLAY_DATE:
                    charArr = new char[]{'-', '-', ' ', ':', ':'};
                    break;
                case DOT_DISPLAY_DATE:
                    charArr = new char[]{'.', '.', ' ', ':', ':'};
                    break;
                case CN_DISPLAY_DATE:
                    charArr = new char[]{'年', '月', '日', ':', ':'};
                    break;
                default:
                    charArr = new char[]{'-', '-', ' ', ':', ':'};
            }
            try
            {
                SimpleDateFormat sdf_1 = null;
                SimpleDateFormat sdf_2 = null;
                switch (dateStr.length())
                {
                    case 6:
                        sdf_1 = new SimpleDateFormat("yyyyMM");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM");
                        break;
                    case 8:
                        sdf_1 = new SimpleDateFormat("yyyyMMdd");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd");
                        break;
                    case 10:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHH");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + "+charArr[2]" + "'HH");
                        break;
                    case 12:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHHmm");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + charArr[2] + "'HH'"
                                + charArr[3] + "'mm");
                        break;
                    case 14:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHHmmss");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + charArr[2] + "'HH'"
                                + charArr[3] + "'mm'" + charArr[4] + "'ss");
                        break;
                    default:
                        return dateStr;
                }
                String strDateT;
                strDateT = sdf_2.format(sdf_1.parse(dateStr));
                if (strDateT.length() > 10)
                {
                    if (formatType != CN_DISPLAY_DATE)
                        strDateT = strDateT.substring(0, strDateT.indexOf(" "));
                    else
                        strDateT = strDateT.substring(0,
                                strDateT.indexOf("日") + 1);
                }
                return strDateT;

            } catch (ParseException ex)
            {
                return dateStr;
            }
        }
    }

    public static String toDisplayTimeStr(String dateStr, int formatType)
    {

        if (formatType < DB_STORE_DATE || formatType > CN_DISPLAY_DATE)
        {
            throw new IllegalArgumentException("时间格式化类型不是合法的值。");
        }
        if (dateStr == null || dateStr.length() < 6 || dateStr.length() > 14
                || formatType == DB_STORE_DATE)
        {
            return dateStr;
        } else
        {
            char[] charArr = null;
            switch (formatType)
            {
                case HYPHEN_DISPLAY_DATE:
                    charArr = new char[]{'-', '-', ' ', ':', ':'};
                    break;
                case DOT_DISPLAY_DATE:
                    charArr = new char[]{'.', '.', ' ', ':', ':'};
                    break;
                case CN_DISPLAY_DATE:

                    charArr = new char[]{'年', '月', ' ', ':', ':'};
                    break;
                default:
                    charArr = new char[]{'-', '-', ' ', ':', ':'};
            }
            try
            {
                SimpleDateFormat sdf_1 = null;
                SimpleDateFormat sdf_2 = null;
                switch (dateStr.length())
                {
                    case 6:
                        sdf_1 = new SimpleDateFormat("yyyyMM");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM");
                        break;
                    case 8:
                        sdf_1 = new SimpleDateFormat("yyyyMMdd");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd");
                        break;
                    case 10:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHH");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + "+charArr[2]" + "'HH");
                        break;
                    case 12:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHHmm");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + charArr[2] + "'HH'"
                                + charArr[3] + "'mm");
                        break;
                    case 14:
                        sdf_1 = new SimpleDateFormat("yyyyMMddHHmmss");
                        sdf_2 = new SimpleDateFormat("yyyy'" + charArr[0] + "'MM'"
                                + charArr[1] + "'dd'" + charArr[2] + "'HH'"
                                + charArr[3] + "'mm'" + charArr[4] + "'ss");
                        break;
                    default:
                        return dateStr;
                }
                String strDateT;
                strDateT = sdf_2.format(sdf_1.parse(dateStr));
                return strDateT;
            } catch (ParseException ex)
            {
                return dateStr;
            }
        }
    }

    /**
     * 得到精确到秒的格式化当前时间串
     *
     * @param formatType 时间格式的类型{@link #DB_STORE_DATE}
     * @return 当前时间格式化时间串
     */
    public static String getCurrTimeStr(int formatType)
    {
        return getTimeStr(new Date(), formatType);
    }

    /**
     * 得到精确到秒的格式化时间串
     *
     * @param date       指定时间
     * @param formatType 时间格式的类型{@link #DB_STORE_DATE}
     * @return 指定时间的格式化时间串
     */
    public static String getTimeStr(Date date, int formatType)
    {

        if (formatType < DB_STORE_DATE || formatType > CN_DISPLAY_DATE)
        {
            throw new IllegalArgumentException("时间格式化类型不是合法的值。");
        } else
        {
            String formatStr = null;
            switch (formatType)
            {
                case DB_STORE_DATE:
                    formatStr = "yyyyMMddHHmmss";
                    break;
                case HYPHEN_DISPLAY_DATE:
                    formatStr = "yyyy'-'MM'-'dd HH:mm:ss";
                    break;
                case DOT_DISPLAY_DATE:
                    formatStr = "yyyy.MM.dd HH:mm:ss";
                    break;
                case CN_DISPLAY_DATE:
                    formatStr = "yyyy'年'MM'月'dd HH:mm:ss";
                    break;
                default :
                    break;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            return sdf.format(date);
        }
    }

    /**
     * 得到精确到天的当前格式化日期串
     *
     * @param formatType 时间格式的类型{@link #DB_STORE_DATE}
     * @return
     */
    public static String getCurrDateStr(int formatType)
    {
        return getDateStr(new Date(), formatType);
    }

    /**
     * 得到精确到天的指定时间格式化日期串
     *
     * @param date       指定时间
     * @param formatType 时间格式的类型{@link #DB_STORE_DATE}
     * @return 指定时间格式化日期串
     */
    public static String getDateStr(Date date, int formatType)
    {
        if (formatType < DB_STORE_DATE || formatType > CN_DISPLAY_DATE)
        {
            throw new IllegalArgumentException("时间格式化类型不是合法的值。");
        } else
        {
            String formatStr = null;
            switch (formatType)
            {
                case DB_STORE_DATE:
                    formatStr = "yyyyMMdd";
                    break;
                case HYPHEN_DISPLAY_DATE:
                    formatStr = "yyyy-MM-dd";
                    break;
                case DOT_DISPLAY_DATE:
                    formatStr = "yyyy.MM.dd";
                    break;
                case CN_DISPLAY_DATE:
                    formatStr = "yyyy'年'MM'月'dd";
                    break;
                default:
                    break;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            return sdf.format(date);
        }
    }

    /**
     * 比较两个时间差的天数,其中一个为默认系统时间,一个为比较的时间 比如2007-10-9,系统当前时间为2007-10-8,则返回1
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static int datenum(Date date) throws ParseException
    {

        long Day = 24L * 60L * 60L * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar startdate = Calendar.getInstance();// 系统当前时间
        String start = sdf.format(new Date());// 格式化时间
        startdate.setTime(sdf.parse(start));// 读取时间

        Calendar enddate = Calendar.getInstance();// 比较的时间
        String end = sdf.format(date);// 格式化时间
        enddate.setTime(sdf.parse(end));// 读取时间

        long comparenum = enddate.getTimeInMillis()
                - startdate.getTimeInMillis();
        int num = (int) (comparenum / Day);
        return num;

    }

    /**
     * 判断两个时间点的差值，精确到秒,当开始时间大于结束时间时返回0
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static int timeLength(Date beginTime, Date endTime)
    {
        int timelength = 0;
        Calendar cBeginTime = Calendar.getInstance();// 系统当前时间
        cBeginTime.setTime(beginTime);//开始时间
        Calendar cEndTime = Calendar.getInstance();// 系统当前时间
        cEndTime.setTime(endTime);//结束时间
        if (beginTime.before(endTime))
        {
            long comparenum = cEndTime.getTimeInMillis() - cBeginTime.getTimeInMillis();
            timelength = (int) (comparenum / 1000);
        }
        return timelength;
    }

    /**
     * 比较两个日期大小
     *
     * @param date1    日期字符串
     * @param pattern1 日期格式
     * @param date2    日期字符串
     * @param pattern2 日期格式
     * @return boolean 若是date1比date2小则返回true
     * @throws ParseException
     */
    public static boolean compareMinDate(String date1, String pattern1,
                                         String date2, String pattern2) throws ParseException
    {
        Date d1 = convertToCalendar(date1, pattern1).getTime();
        Date d2 = convertToCalendar(date2, pattern2).getTime();
        return d1.before(d2);
    }

    /**
     * 根据传入的日期字符串以及格式，产生一个Calendar对象
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return Calendar
     * @throws ParseException 当格式与日期字符串不匹配时抛出该异常
     */
    public static Calendar convertToCalendar(String date, String pattern)
            throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date d = sdf.parse(date);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(d);
        return calendar;
    }

    public static String formateByPattern(Date date, String pattern)
    {
        SimpleDateFormat sdfDate = new SimpleDateFormat(pattern);
        return sdfDate.format(date);
    }

    /**
     * 格式化日期,返回格式为:dd.MM.yyyy
     *
     * @param date
     * @return
     */
    public static String formateDate(Date date)
    {
        return formateByPattern(date, "dd.MM.yyyy");
    }

    /**
     * 格式化日期,返回格式为:HH:mm
     *
     * @param date
     * @return
     */
    public static String formateTime(Date date)
    {
        return formateByPattern(date, "HH:mm");
    }

    /**
     * 格式化日期,返回格式为:HH:mm
     *
     * @param date
     * @return
     */
    public static String formateTimeHMS(Date date)
    {
        return formateByPattern(date, "HH:mm:ss");
    }

    /**
     * 格式化日期,返回格式为:MMddHHmmss
     *
     * @param date
     * @return
     */
    public static String formateMessageDate(Date date)
    {
        return formateByPattern(date, "MMddHHmmss");
    }

    /**
     * 获得当前日期与本周一相差的天数
     *
     * @return
     */
    public static int getMondayPlus()
    {
        Calendar cal = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1)
        {
            return -6;
        } else
        {
            return 2 - dayOfWeek;
        }
    }

    /**
     * 获得上周星期一的Date，具体为周一的00:00:00
     *
     * @param weeks 表示上一周还是上两周，-2:上两周，-1：上一周，0：本周，1：下周，2：下两周
     * @return
     */
    public static Date getPreviousMonday(int weeks)
    {
        int mondayPlus = DateUtils.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        currentDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        currentDate.set(GregorianCalendar.MINUTE, 0);
        currentDate.set(GregorianCalendar.SECOND, 0);
        Date monday = currentDate.getTime();
        return monday;
    }

    /**
     * 获得上周星期日的Date，具体为周日的23:59:59
     *
     * @param weeks 表示上一周还是上两周，-2:上两周，-1：上一周，0：本周，1：下周，2：下两周
     * @return
     */
    public static Date getPreviousSunday(int weeks)
    {
        Date monday = DateUtils.getPreviousMonday(weeks);

        Calendar sunCal = Calendar.getInstance();
        sunCal.setTime(monday);
        sunCal.add(Calendar.DATE, 6);
        sunCal.set(GregorianCalendar.HOUR_OF_DAY, 23);
        sunCal.set(GregorianCalendar.MINUTE, 59);
        sunCal.set(GregorianCalendar.SECOND, 59);

        return sunCal.getTime();
    }

    /**
     * 获得上月1号的Date，具体为1号的00:00:00
     *
     * @param months 表示上一月还是上两月，-2:上两月，-1：上一月，0：本月，1：下月，2：下两月
     * @return
     */
    public static Date getPriviousMonthFirstDay(int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得上月最后一天的Date，具体为最后一天的23:59:59
     *
     * @param months 表示上一月还是上两月，-2:上两月，-1：上一月，0：本月，1：下月，2：下两月
     * @return
     */
    public static Date getPriviousMonthLastDay(int months)
    {
        Date firstDayInMon = DateUtils.getPriviousMonthFirstDay(months);

        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDayInMon);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
        cal.set(GregorianCalendar.MINUTE, 59);
        cal.set(GregorianCalendar.SECOND, 59);

        return cal.getTime();
    }

    /**
     * 传入今天值,返回昨天值,格式为yyyy-mm-dd
     *
     * @return
     */
    public static String getYesterday()
    {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);
        Date Yesterday = cal.getTime();
        return formateByPattern(Yesterday, "yyyy-MM-dd");
    }

    /**
     * 取得上一周的周六和周日时间
     * beforeweek[0]:上一周周六，beforeweek[1]：上一周周日
     *
     * @return
     */
    public static String[] getBeforeWeek()
    {
        String[] beforeweek = new String[2];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int day = c.get(Calendar.DAY_OF_WEEK);
        if (day == 1)
            day = 8;
        c.add(Calendar.DATE, -day);
        beforeweek[0] = sdf.format(c.getTime());
        c.add(Calendar.DATE, 1);
        beforeweek[1] = sdf.format(c.getTime());
        return beforeweek;
    }

    /**
     * 返回某月某日，例：09月27日
     *
     * @param dateStr
     * @param formatType
     * @return
     */
    public static String toDisplayMonthAndDate(String dateStr, int formatType)
    {
        char[] charArr = new char[]{'月', '日'};
        try
        {
            SimpleDateFormat sdf_1 = null;
            SimpleDateFormat sdf_2 = null;
            if (dateStr.length() == 8)
            {
                sdf_1 = new SimpleDateFormat("yyyyMMdd");
                sdf_2 = new SimpleDateFormat("MM'"
                        + charArr[0] + "'dd" + charArr[1]);
            }
            String strDateT;
            strDateT = sdf_2.format(sdf_1.parse(dateStr));
            return strDateT;
        } catch (ParseException ex)
        {
            return dateStr;
        }
    }

    /**
     * 取得当前小时数
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getNowDayHour()
    {
        Date date = new Date();
        int hours = date.getHours();
        return hours;
    }

    /**
     * 转换格式为yyyy-mm-dd HH:mm:ss的日期字符串为制定格式日期对象
     * 如date:2010-10-10 00:00:00,format:yyyy-mm-dd HH:mm:ss
     *
     * @param date
     * @param format
     * @return
     */
    public static Date convertFromStringToDate(String date, String format)
    {
        Date newDate = null;
        DateFormat fmt = new SimpleDateFormat(format);
        try
        {
            newDate = fmt.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return newDate;
    }
}
