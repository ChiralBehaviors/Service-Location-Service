/**
 * Copyright (C) 2010 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.slp;

/**
 * @author hhildebrand
 * 
 */
public enum Protocol {
    TCP("_tcp"), UDP("_udp");

    /**
     * @param label
     * @return
     */
    public static Protocol from(String label) {
        if (TCP.getType().equals(label)) {
            return TCP;
        } else if (UDP.getType().equals(label)) {
            return UDP;
        }
        throw new IllegalArgumentException(
                                           String.format("%s is not a valid protocol label"));
    }

    /**
     * Answer true if the name component represents a protocol
     */
    public static boolean isProtocol(String component) {
        return TCP.type.equals(component) || UDP.type.equals(component);
    }

    private final String type;

    private Protocol(String protocolType) {
        type = protocolType;
    }

    public String getType() {
        return type;
    }
}
