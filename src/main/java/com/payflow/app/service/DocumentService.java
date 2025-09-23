package com.payflow.app.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.payflow.app.entity.Document;
import com.payflow.app.entity.Organization;
import com.payflow.app.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final Cloudinary cloudinary;
	private final DocumentRepository documentRepository;

	public Document uploadOrganizationDocument(MultipartFile file, String purpose, Organization org)
			throws IOException {
		Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
				ObjectUtils.asMap("folder", "organizations/" + org.getId()));

		Document doc = Document.builder().cloudinaryPublicId((String) result.get("public_id"))
				.secureUrl((String) result.get("secure_url")).purpose(purpose).organization(org).build();

		return documentRepository.save(doc);
	}
}
