/**
 * Mainbo.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.tmser.tomcat;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;

/**
 * <pre>
 *
 * </pre>
 *
 * @author 3020mt
 * @version $Id: MbSession.java, v 1.0 2017年3月24日 下午3:20:28 3020mt Exp $
 */
public class MbSession extends StandardSession {

  private static final long serialVersionUID = -6011950518228946125L;

  /**
   * @param manager
   */
  public MbSession(Manager manager) {
    super(manager);
  }

  @Override
  public void setAttribute(String name, Object value) {
    setAttribute(name, value, true);
    super.access();
  }
}
