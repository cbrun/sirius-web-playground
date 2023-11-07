package fr.obeo.playground.restfulemf.sample.services;

import java.util.List;

import org.eclipse.sirius.components.core.api.IImagePathService;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link IImagePathService} for the Flow domain.
 *
 * @author lfasani
 */
@Service
public class ImagePathService implements IImagePathService {

    private static final List<String> IMAGES_PATHS = List.of("/img", "/images", "/icons");

    @Override
    public List<String> getPaths() {
        return IMAGES_PATHS;
    }

}
