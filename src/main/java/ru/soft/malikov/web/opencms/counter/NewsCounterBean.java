package ru.soft.malikov.web.opencms.counter;

import org.opencms.jsp.CmsJspActionElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class NewsCounterBean extends CmsJspActionElement {

    /**
     * Empty constructor, required for every JavaBean.
     */
    public NewsCounterBean() {
        super();
    }

    /**
     * Constructor, with parameters.
     *
     * @param context the JSP page context object
     * @param req     the JSP request
     * @param res     the JSP response
     */
    public NewsCounterBean(PageContext context, HttpServletRequest req, HttpServletResponse res) {
        super(context, req, res);
    }

    public NewsCounterManager getCounterManager() {
        NewsCounterManager manager = new NewsCounterManager();
        return manager;
    }

    public String getIncrementCounter(String counterKey) {
        return String.valueOf(getCounterManager().incrementCounter(counterKey));
    }

    public String getCounterValue(String counterKey) {
        return String.valueOf(getCounterManager().getCounterValue(counterKey));
    }
}
