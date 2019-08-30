package ru.soft.malikov.web.opencms.counter;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsResource;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsUUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class NewsCounterBean extends CmsJspActionElement {

    private static final Log LOG = CmsLog.getLog(NewsCounterBean.class);

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

    public String getIncrementCounter(String contFilename) {
        String counterKey = getKeyViaPath(contFilename);
        return String.valueOf(getCounterManager().incrementCounter(counterKey));
    }

    public String getCounterValue(String contFilename) {
        String counterKey = getKeyViaPath(contFilename);
        return String.valueOf(getCounterManager().getCounterValue(counterKey));
    }

    private String getKeyViaPath(String contFilename) {
        try {
            CmsResource res = this.getCmsObject().readResource(contFilename);
            CmsUUID rid = res.getResourceId();
            return rid.getStringValue();
        } catch (CmsException e) {
            LOG.error(e);
        }
        return "";
    }
}
