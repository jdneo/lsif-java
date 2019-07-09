///* --------------------------------------------------------------------------------------------
// * Copyright (c) Microsoft Corporation. All rights reserved.
// * Licensed under the MIT License. See License.txt in the project root for license information.
// * ------------------------------------------------------------------------------------------ */
//
//package com.microsoft.java.lsif.core.internal.visitors;
//
//import java.util.List;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.eclipse.core.runtime.NullProgressMonitor;
//import org.eclipse.jdt.core.IJavaElement;
//import org.eclipse.jdt.core.dom.SimpleName;
//import org.eclipse.jdt.core.dom.SimpleType;
//import org.eclipse.jdt.ls.core.internal.JDTUtils;
//import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
//import org.eclipse.jdt.ls.core.internal.handlers.HoverHandler;
//import org.eclipse.jdt.ls.core.internal.preferences.PreferenceManager;
//import org.eclipse.lsp4j.Hover;
//import org.eclipse.lsp4j.Location;
//import org.eclipse.lsp4j.MarkedString;
//import org.eclipse.lsp4j.MarkupContent;
//import org.eclipse.lsp4j.Position;
//import org.eclipse.lsp4j.TextDocumentIdentifier;
//import org.eclipse.lsp4j.TextDocumentPositionParams;
//import org.eclipse.lsp4j.jsonrpc.messages.Either;
//
//import com.microsoft.java.lsif.core.internal.JdtlsUtils;
//import com.microsoft.java.lsif.core.internal.LanguageServerIndexerPlugin;
//import com.microsoft.java.lsif.core.internal.emitter.LsifEmitter;
//import com.microsoft.java.lsif.core.internal.indexer.IndexerContext;
//import com.microsoft.java.lsif.core.internal.indexer.LsifService;
//import com.microsoft.java.lsif.core.internal.indexer.Repository;
//import com.microsoft.java.lsif.core.internal.protocol.DefinitionResult;
//import com.microsoft.java.lsif.core.internal.protocol.Document;
//import com.microsoft.java.lsif.core.internal.protocol.HoverResult;
//import com.microsoft.java.lsif.core.internal.protocol.Range;
//import com.microsoft.java.lsif.core.internal.protocol.ReferenceItem;
//import com.microsoft.java.lsif.core.internal.protocol.ReferenceResult;
//import com.microsoft.java.lsif.core.internal.protocol.ResultSet;
//import com.microsoft.java.lsif.core.internal.protocol.SymbolData;
//
//public class DefinitionVisitor extends ProtocolVisitor {
//
//	public DefinitionVisitor(LsifService lsif, IndexerContext context) {
//		super(lsif, context);
//	}
//
//	@Override
//	public boolean visit(SimpleName node) {
//		emitDefinition(node.getStartPosition(), node.getLength());
//		return false;
//	}
//
//	@Override
//	public boolean visit(SimpleType node) {
//		emitDefinition(node.getStartPosition(), node.getLength());
//		return false;
//	}
//
//	private void emitDefinition(int startPosition, int length) {
//		try {
//			org.eclipse.lsp4j.Range fromRange = JDTUtils.toRange(this.getContext().getCompilationUnit().getTypeRoot(),
//					startPosition,
//					length);
//
//			IJavaElement element = JDTUtils.findElementAtSelection(this.getContext().getCompilationUnit().getTypeRoot(),
//					fromRange.getStart().getLine(), fromRange.getStart().getCharacter(), new PreferenceManager(),
//					new NullProgressMonitor());
//			if (element == null) {
//				return;
//			}
//
//			Location targetLocation = JdtlsUtils.getElementLocation(element);
//			if (targetLocation == null) {
//				// not target location, only resolve hover.
//				Hover hover = hover(fromRange.getStart().getLine(), fromRange.getStart().getCharacter());
//				if (isEmptyHover(hover)) {
//					return;
//				}
//
//				LsifService lsif = this.getLsif();
//				Document docVertex = this.getContext().getDocVertex();
//
//				// Source range:
//				Range sourceRange = Repository.getInstance().enlistRange(lsif, docVertex, fromRange);
//
//				// Result set
//				ResultSet resultSet = Repository.getInstance().enlistResultSet(lsif, sourceRange);
//
//				HoverResult hoverResult = lsif.getVertexBuilder().hoverResult(hover);
//				LsifEmitter.getInstance().emit(hoverResult);
//				LsifEmitter.getInstance().emit(lsif.getEdgeBuilder().hover(resultSet, hoverResult));
//				return;
//			}
//
//			LsifService lsif = this.getLsif();
//			Document docVertex = this.getContext().getDocVertex();
//
//			// Source range:
//			Range sourceRange = Repository.getInstance().enlistRange(lsif, docVertex, fromRange);
//
//			// Target range:
//			org.eclipse.lsp4j.Range toRange = targetLocation.getRange();
//			Document targetDocument = Repository.getInstance().enlistDocument(lsif, targetLocation.getUri());
//			Range targetRange = Repository.getInstance().enlistRange(lsif, targetDocument, toRange);
//
//			// Result set
//			ResultSet resultSet = Repository.getInstance().enlistResultSet(lsif, sourceRange);
//
//			// Link resultSet & definitionResult
//			DefinitionResult defResult = lsif.getVertexBuilder().definitionResult(targetRange.getId());
//			LsifEmitter.getInstance().emit(defResult);
//			LsifEmitter.getInstance().emit(lsif.getEdgeBuilder().definition(resultSet, defResult));
//
//			/* start reference */
//			String id = createSymbolKey(targetLocation);
//			SymbolData symbolData = Repository.getInstance().enlistSymbolData(lsif, id);
//			ReferenceResult referenceResult = symbolData.getReferenceResult();
//			if (referenceResult == null) {
//				referenceResult = lsif.getVertexBuilder().referenceResult();
//				symbolData.setReferenceResult(referenceResult);
//				LsifEmitter.getInstance().emit(referenceResult);
//			}
//			LsifEmitter.getInstance().emit(lsif.getEdgeBuilder().references(resultSet, referenceResult));
//			if (!JdtlsUtils.normalizeUri(targetDocument.getUri()).equals(JdtlsUtils.normalizeUri(docVertex.getUri()))
//					|| !sourceRange.equals(targetRange)) {
//				LsifEmitter.getInstance().emit(
//						lsif.getEdgeBuilder().referenceItem(referenceResult, sourceRange, ReferenceItem.REFERENCE));
//			}
//
////			ReferenceResult refResult = lsif.getVertexBuilder().referenceResult();
////			symbolData.getReferenceResults().add(refResult);
////			LsifEmitter.getInstance().emit(refResult);
////			LsifEmitter.getInstance().emit(lsif.getEdgeBuilder().references(resultSet, refResult));
////			for (Range range : symbolData.getSourceRanges()) {
////				LsifEmitter.getInstance()
////						.emit(lsif.getEdgeBuilder().referenceItem(refResult, range, ReferenceItem.REFERENCE));
////			}
////			if (JdtlsUtils.normalizeUri(docVertex.getUri()).equals(JdtlsUtils.normalizeUri(targetLocation.getUri()))
////					&& sourceRange.equals(targetRange)) {
////				return;
////			}
////			symbolData.getSourceRanges().add(sourceRange);
////			for (ReferenceResult result : symbolData.getReferenceResults()) {
////				LsifEmitter.getInstance()
////						.emit(lsif.getEdgeBuilder().referenceItem(result, sourceRange, ReferenceItem.REFERENCE));
////			}
//
//			/* start hover */
//			HoverResult hoverResult = symbolData.getHoverResult();
//			if (hoverResult == null) {
//				Hover hover = hover(fromRange.getStart().getLine(), fromRange.getStart().getCharacter());
//				if (isEmptyHover(hover)) {
//					return;
//				}
//				hoverResult = lsif.getVertexBuilder().hoverResult(hover);
//				LsifEmitter.getInstance().emit(hoverResult);
//				symbolData.setHoverResult(hoverResult);
//			}
//
//			LsifEmitter.getInstance().emit(lsif.getEdgeBuilder().hover(resultSet, hoverResult));
//		} catch (Throwable ex) {
//			LanguageServerIndexerPlugin.logException("Exception when dumping definition information ", ex);
//		}
//	}
//
//	private String createSymbolKey(Location definitionLocation) {
//		String rawKeyString = definitionLocation.toString();
//		return DigestUtils.md5Hex(rawKeyString);
//	}
//
//	private Hover hover(int line, int character) {
//		TextDocumentPositionParams params = new TextDocumentPositionParams(
//				new TextDocumentIdentifier(this.getContext().getDocVertex().getUri()), new Position(line, character));
//
//		HoverHandler proxy = new HoverHandler(JavaLanguageServerPlugin.getPreferencesManager());
//		return proxy.hover(params, new NullProgressMonitor());
//	}
//
//	private boolean isEmptyHover(Hover hover) {
//		Either<List<Either<String, MarkedString>>, MarkupContent> content = hover.getContents();
//		if (content == null) {
//			return true;
//		}
//
//		if (content.isRight()) {
//			return false;
//		}
//
//		List<Either<String, MarkedString>> list = content.getLeft();
//		if (list == null || list.size() == 0) {
//			return true;
//		}
//
//		for (Either<String, MarkedString> either : list) {
//			if (StringUtils.isNotEmpty(either.getLeft()) || either.isRight()) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//}
