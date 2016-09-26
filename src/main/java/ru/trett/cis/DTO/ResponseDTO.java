/*
 *     CIS - cool inventory system
 *
 *     Copyright © 2016 Roman Tretyakov <roman@trett.ru>
 *
 *     ********************************************************************
 *
 *     CIS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CIS.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.trett.cis.DTO;

import ru.trett.cis.utils.EnumUtil;

/**
 * DTO for ajax response
 */
public class ResponseDTO {

    private String messageBody;

    private String customData;

    private MessageLevel messageLevel;

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public MessageLevel getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = EnumUtil.lookup(MessageLevel.class, messageLevel);
    }

    public void setMessage(String messageBody, String messageLevel) {
        setMessageBody(messageBody);
        setMessageLevel(messageLevel);
    }

    public enum MessageLevel {

        SUCCESS, INFO, WARNING, DANGER

    }

}
