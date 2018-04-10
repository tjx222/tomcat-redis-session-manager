/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.tomcat;

import org.apache.catalina.session.StandardManager;
import org.apache.catalina.session.StandardSession;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: MbSessManager.java, v 1.0 2017年3月24日 下午3:17:19 tmser Exp $
 */
public class MbSessionManager extends StandardManager {

  @Override
  protected StandardSession getNewSession() {
    return new MbSession(this);
  }

}
