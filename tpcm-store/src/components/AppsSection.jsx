import React, { useState } from "react";
import { Link } from "react-router-dom";
import { getPaginatedGames, getCategories } from "../data/gamesData";

function AppsSection() {
    const [currentPage, setCurrentPage] = useState(1);
    const [selectedCategory, setSelectedCategory] = useState("all");
    const itemsPerPage = 9;

    const categories = getCategories();
    const { games, totalPages, totalItems } = getPaginatedGames(
        currentPage,
        itemsPerPage,
        selectedCategory
    );

    const handlePageChange = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const handleCategoryChange = (category) => {
        setSelectedCategory(category);
        setCurrentPage(1);
    };

    const renderPagination = () => {
        if (totalPages <= 1) return null;

        const pages = [];
        for (let i = 1; i <= totalPages; i++) {
            pages.push(
                <button
                    key={i}
                    className={`btn btn-sm mx-1 ${
                        i === currentPage
                            ? "btn-warning"
                            : "btn-outline-secondary"
                    }`}
                    onClick={() => handlePageChange(i)}
                    style={
                        i === currentPage
                            ? {
                                  backgroundColor: "#ff7a00",
                                  border: "none",
                                  color: "#000",
                              }
                            : {}
                    }
                >
                    {i}
                </button>
            );
        }

        return (
            <div className="d-flex justify-content-center align-items-center mt-4">
                <button
                    className="btn btn-outline-secondary btn-sm me-2"
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                >
                    <i className="bi bi-chevron-left"></i> Previous
                </button>

                {pages}

                <button
                    className="btn btn-outline-secondary btn-sm ms-2"
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                >
                    Next <i className="bi bi-chevron-right"></i>
                </button>
            </div>
        );
    };

    return (
        <div>
            <div className="text-center mb-4">
                <h2 className="fw-bold" style={{ color: "#212529" }}>
                    Apps & Games
                </h2>
                <p className="text-muted">
                    Download your favourite apps and games - {totalItems}{" "}
                    available
                </p>
            </div>

            <div className="mb-4 text-center">
                <div className="btn-group" role="group">
                    {categories.map((category) => (
                        <button
                            key={category}
                            type="button"
                            className={`btn ${
                                selectedCategory === category
                                    ? "btn-warning"
                                    : "btn-outline-secondary"
                            }`}
                            onClick={() => handleCategoryChange(category)}
                            style={
                                selectedCategory === category
                                    ? {
                                          backgroundColor: "#ff7a00",
                                          border: "none",
                                          color: "#000",
                                      }
                                    : {}
                            }
                        >
                            {category.charAt(0).toUpperCase() +
                                category.slice(1)}
                        </button>
                    ))}
                </div>
            </div>

            <div className="row g-4">
                {games.map((app) => (
                    <div key={app.id} className="col-lg-4 col-md-6">
                        <div
                            className="card h-100 border-0 shadow-sm"
                            style={{
                                backgroundColor: "#212529",
                                borderRadius: "15px",
                            }}
                        >
                            <img
                                src={app.image}
                                alt={app.title}
                                className="card-img-top"
                                style={{
                                    height: "200px",
                                    objectFit: "cover",
                                    borderRadius: "15px 15px 0 0",
                                }}
                            />
                            <div className="card-body p-4 text-light d-flex flex-column">
                                <h5
                                    className="card-title text-white"
                                    style={{ minHeight: "3rem" }}
                                >
                                    {app.title}
                                </h5>
                                <p
                                    className="card-text text-light flex-grow-1"
                                    style={{
                                        fontSize: "0.9rem",
                                        minHeight: "3rem",
                                        opacity: 0.9,
                                    }}
                                >
                                    {app.description}
                                </p>

                                {app.features && app.features.length > 0 && (
                                    <div className="mb-3">
                                        {app.features
                                            .slice(0, 2)
                                            .map((feature, index) => (
                                                <small
                                                    key={index}
                                                    className="d-block text-light"
                                                    style={{ opacity: 0.8 }}
                                                >
                                                    <i className="bi bi-check-circle me-1 text-success"></i>
                                                    {feature}
                                                </small>
                                            ))}
                                    </div>
                                )}

                                <div className="d-flex justify-content-between align-items-center mt-auto">
                                    <span className="text-warning fw-bold">
                                        ${app.price}
                                    </span>
                                    <Link
                                        to={`/app/${app.id}`}
                                        className="btn btn-sm"
                                        style={{
                                            backgroundColor: "#ff7a00",
                                            color: "#000",
                                            textDecoration: "none",
                                        }}
                                    >
                                        <i className="bi bi-cart-plus me-1"></i>
                                        View Details
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {renderPagination()}
        </div>
    );
}

export default AppsSection;
