package com.fastjrun.codeg.generator.method;

import java.util.List;

import com.fastjrun.codeg.common.PacketField;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class DefaultHTTPMethodGenerator extends BaseHTTPMethodGenerator {

    /**
     * app,api
     *
     * @param jInvocation
     */
    @Override
    protected void processExtraParameters(JInvocation jInvocation) {
        List<PacketField> etraParameters = this.serviceMethodGenerator.getCommonMethod().getExtraParameters();
        if (etraParameters != null && etraParameters.size() > 0) {
            for (int index = 0; index < etraParameters.size(); index++) {
                PacketField parameter = etraParameters.get(index);
                jInvocation.arg(JExpr.lit(parameter.getName()));
            }
        }
    }
}