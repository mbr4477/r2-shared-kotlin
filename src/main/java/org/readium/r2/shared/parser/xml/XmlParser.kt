/*
 * Module: r2-shared-kotlin
 * Developers: Aferdita Muriqi, Clément Baumann
 *
 * Copyright (c) 2018. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.shared.parser.xml

import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XmlParser {

    private var nodes: MutableList<Node> = mutableListOf()

    fun getFirst(name: String) = try {
        nodes.first { it.name == name }
    } catch (e: Exception) {
        null
    }

    fun root() = nodes.firstOrNull() ?: throw Exception("No root in xml document")

    fun parseXml(stream: InputStream) {
        nodes = mutableListOf()
        val parser = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
        val document = parser.parse(stream)

        nodes.add(createNodeFromDocumentNode(document))
    }

    private fun createNodeFromDocumentNode(docNode: org.w3c.dom.Node): Node {
        if (docNode.nodeType == org.w3c.dom.Node.DOCUMENT_NODE)
            return createNodeFromDocumentNode(docNode.firstChild)
        val node = Node(docNode.nodeName)
        node.text = docNode.textContent
        if (docNode.attributes != null && docNode.hasAttributes()) {
            for (j in 0 until docNode.attributes.length) {
                val attribute = docNode.attributes.item(j)
                node.attributes[attribute.nodeName] = attribute.nodeValue
            }
        }
        if (docNode.childNodes != null && docNode.hasChildNodes()) {
            for (i in 0 until docNode.childNodes.length) {
                val child = docNode.childNodes.item(i)
                when (child.nodeType) {
//                    org.w3c.dom.Node.TEXT_NODE -> node.text = node.text ?: "" + child.textContent
                    org.w3c.dom.Node.ELEMENT_NODE -> {
                        node.children.add(createNodeFromDocumentNode(child))
                    }
                }
            }
        }
        return node
    }
}